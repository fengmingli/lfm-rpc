package com.lifengming.rpc.register.loadbalance;

/**
 * @author hongbin
 * Created on 18/11/2017
 */
public interface LoadBalancer<T> {

	/**
	 *
	 * @return  T
	 */
	T next();
}
