package com.lifengming.rpc.register.loadbalance.impl;


import com.lifengming.rpc.common.annotation.LoadBalanceSupport;
import com.lifengming.rpc.common.constans.RpcConstant;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机算法
 *
 * 在 serviceAddresses.size() 中随机获取一个 ServiceAddress
 *
 * @author hongbin
 * Created on 18/11/2017
 */

@LoadBalanceSupport(RpcConstant.BALANCE_RANDOM)
public class RandomBalancer implements LoadBalancer<Service> {

	@Override
	public Service next(List<Service> services) {
		if (services.size() == 0) {
			return null;
		}
		return services.get(ThreadLocalRandom.current().nextInt(services.size()));
	}
}
