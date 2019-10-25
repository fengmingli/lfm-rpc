package com.lifengming.rpc.register.servicecenter;

/**
 * 服务发现接口
 * <p></>
 * @author lifengming
 * @since 2019.10.23
 */
public interface ServiceDiscovery {
    /**
     * 通过服务名发现服务（信息）
     *
     * @param serviceName 服务名
     * @return string
     */
    String serviceDiscovery(String serviceName);
}
