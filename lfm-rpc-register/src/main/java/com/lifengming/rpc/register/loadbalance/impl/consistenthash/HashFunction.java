package com.lifengming.rpc.register.loadbalance.impl.consistenthash;

/**
 * @author hongbin
 * Created on 19/11/2017
 */
public interface HashFunction<T> {
	int hash(T t);
}
