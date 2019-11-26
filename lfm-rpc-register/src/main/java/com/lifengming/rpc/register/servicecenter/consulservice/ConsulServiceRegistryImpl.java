package com.lifengming.rpc.register.servicecenter.consulservice;


import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.agent.model.NewService;
import com.lifengming.rpc.core.model.ServiceAddress;
import com.lifengming.rpc.register.servicecenter.ServiceRegistry;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * @author hongbin
 * Created on 21/10/2017
 */

public class ConsulServiceRegistryImpl implements ServiceRegistry {

	private ConsulClient consulClient;

	public ConsulServiceRegistryImpl(@NonNull String consulAddress) {
		String[] address = consulAddress.split(":");
		ConsulRawClient rawClient = new ConsulRawClient(address[0], Integer.valueOf(address[1]));
		consulClient = new ConsulClient(rawClient);
	}

	@Override
	public void serviceRegister(String serviceName, ServiceAddress serviceAddress) {
		NewService newService = new NewService();
		newService.setId(serviceName);
		newService.setName(serviceName);
		ArrayList<String> tags = new ArrayList();
		tags.add("urlprefix-/"+serviceName);
		newService.setTags(tags);
		newService.setAddress(serviceAddress.getIp());
		newService.setPort(serviceAddress.getPort());

		// TODO: make check configurable
		NewService.Check check = new NewService.Check();
		check.setTcp(serviceAddress.toString());
		check.setInterval("1s");
		newService.setCheck(check);
		consulClient.agentServiceRegister(newService);
	}

	private String generateNewIdForService(String serviceName, ServiceAddress serviceAddress){
		// serviceName + ip + port
		return serviceName + "-" + serviceAddress.getIp() + "-" + serviceAddress.getPort();
	}
}
