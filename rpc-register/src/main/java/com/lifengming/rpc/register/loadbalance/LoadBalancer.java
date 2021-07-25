package com.lifengming.rpc.register.loadbalance;

import java.util.List;

/**
 * @author hongbin
 * Created on 18/11/2017
 */
public interface LoadBalancer<T> {

	/**
	 * 负载均衡，获取一个可用的服务
	 * @param list  服务
	 * @return  范型
	 */
	T next(List<T> list);
}
