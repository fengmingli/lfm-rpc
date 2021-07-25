package com.lifengming.rpc.register.loadbalance.impl;

import com.lifengming.rpc.common.annotation.LoadBalanceSupport;
import com.lifengming.rpc.common.constans.RpcConstant;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;

import java.util.List;

/**
 * 轮询算法
 *
 * @author lifengming
 * @date 2021.07.25
 */
@LoadBalanceSupport(RpcConstant.BALANCE_ROUND)
public class FullRoundBalance implements LoadBalancer<Service> {
    private int index;

    @Override
    public synchronized Service next(List<Service> services) {
        // 加锁防止多线程情况下，index超出services.size()
        if (index == services.size()) {
            index = 0;
        }
        return services.get(index++);
    }
}
