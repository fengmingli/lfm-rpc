package com.lifengming.rpc.register.loadbalance.impl;


import com.lifengming.rpc.core.model.ServiceAddress;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author hongbin
 * Created on 18/11/2017
 */
@Data
@Builder
@AllArgsConstructor
public class RandomLoadBalancer implements LoadBalancer<ServiceAddress> {

	List<ServiceAddress> serviceAddresses;

	@Override
	public ServiceAddress next() {
		if (serviceAddresses.size() == 0) {
			return null;
		}
		return serviceAddresses.get(ThreadLocalRandom.current().nextInt(serviceAddresses.size()));
	}
}
