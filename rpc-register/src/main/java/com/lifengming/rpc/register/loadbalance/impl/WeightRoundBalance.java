package com.lifengming.rpc.register.loadbalance.impl;

import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.register.loadbalance.LoadBalancer;

import java.util.List;

/**
 * 加权轮询
 *
 * @author lifengming
 * @date 2021.07.25
 */
public class WeightRoundBalance implements LoadBalancer<Service> {
    private int index;

    @Override
    public Service next(List<Service> services) {
        int allWeight = services.stream()
                .mapToInt(Service::getWeight)
                .sum();

        int number = (index++) % allWeight;

        for (Service service : services) {
            if (service.getWeight() > number) {
                return service;
            }
            number -= service.getWeight();
        }
        return null;
    }
}
