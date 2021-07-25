package com.lifengming.rpc.register.localcache;

import com.lifengming.rpc.common.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 *     服务本地缓存
 * </p>
 * @author lifengming
 * @date 2021.07.25
 */
public class ServiceLocalCache {
    /**
     * key: serviceName
     * value: 服务列表
     */
    private static final Map<String, List<Service>> SERVER_MAP = new ConcurrentHashMap<>();

    /**
     * 客户端注入的远程服务service class
     */
    public static final List<String> SERVICE_CLASS_NAMES = new ArrayList<>();

    public static void put(String serviceName, List<Service> serviceList) {
        SERVER_MAP.put(serviceName, serviceList);
    }

    /**
     * 去除指定的值
     * @param serviceName 服务名
     * @param service 服务元数据信息
     */
    public static void remove(String serviceName, Service service) {
        SERVER_MAP.computeIfPresent(serviceName, (key, value) ->
                value.stream()
                        .filter(o -> !o.toString().equals(service.toString()))
                        .collect(Collectors.toList())
        );
    }

    /**
     * 移除所有服务
     * @param serviceName 服务名
     */
    public static void removeAll(String serviceName) {
        SERVER_MAP.remove(serviceName);
    }


    /**
     * 判断服务是否存在
     * @param serviceName 服务名
     * @return true 存在：false 不存在
     */
    public static boolean isEmpty(String serviceName) {
        return SERVER_MAP.get(serviceName) == null || SERVER_MAP.get(serviceName).size() == 0;
    }

    /**
     * 根据服务名获取服务信息
     * @param serviceName 服务名
     * @return List<Service> 服务列表
     */
    public static List<Service> getServicesByServiceName(String serviceName) {
        return SERVER_MAP.get(serviceName);
    }

}
