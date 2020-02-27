package com.lifengming.rpc.register.servicecenter.consulservice;


import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.agent.model.Service;
import com.ecwid.consul.v1.health.model.HealthService;
import com.lifengming.rpc.core.model.ServiceAddress;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;
import com.lifengming.rpc.register.loadbalance.impl.RandomLoadBalancer;
import com.lifengming.rpc.register.servicecenter.ServiceDiscovery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author hongbin
 * Created on 21/10/2017
 */
@Slf4j
@Data
public class ConsulServiceDiscoveryImpl implements ServiceDiscovery {

	public String discoveryAddress;

	private ConsulClient consulClient;

	Map<String, ServiceAddress> loadBalancerMap = new ConcurrentHashMap<>();

	public ConsulServiceDiscoveryImpl(String consulAdress) {
		String[] address = consulAdress.split(":");
		ConsulRawClient rawClient = new ConsulRawClient(address[0], Integer.valueOf(address[1]));
		consulClient = new ConsulClient(rawClient);
	}

	@Override
	public String serviceDiscovery(String serviceName) {
		if (!loadBalancerMap.containsKey(serviceName) || loadBalancerMap.get(serviceName) == null) {
			Service service = consulClient.getAgentServices().getValue().get(serviceName);
			ServiceAddress serviceAddress = new ServiceAddress(service.getAddress(),service.getPort());
			loadBalancerMap.put(serviceName,serviceAddress);

			// Watch consul
			longPolling(serviceName);
		}

		ServiceAddress loadBalAddress = loadBalancerMap.get(serviceName);
		if (loadBalAddress == null) {
			throw new RuntimeException(String.format("No service instance for %s", serviceName));
		}

		return loadBalAddress.toString();
	}

	private void longPolling(String serviceName){
		new Thread(() -> {
			long consulIndex = -1;
			do {

				Service service = consulClient.getAgentServices().getValue().get(serviceName);
				ServiceAddress serviceAddress = new ServiceAddress(service.getAddress(),service.getPort());
				loadBalancerMap.put(serviceName,serviceAddress);
			} while(true);
		}).start();
	}

	private LoadBalancer buildLoadBalancer(List<HealthService> healthServices) {
		// TODO: make load balancer type configurable
		return new RandomLoadBalancer(healthServices.stream()
				.map(healthService -> {
					HealthService.Service service =healthService.getService();
					return new ServiceAddress(service.getAddress() , service.getPort());
				})
				.collect(Collectors.toList()));
	}
}
