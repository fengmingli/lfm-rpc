package com.lifengming.rpc.register;

import com.lifengming.rpc.common.model.Service;

import java.util.List;

/**
 * 服务发现
 *
 * @author lifengming
 * @date 2021.07.25
 */
public interface ServerDiscovery {

    /**
     * 通过服务名获取服务注册的列表
     * @param serviceName 服务名
     * @return 服务列表 List<Service>
     */
    List<Service> getServiceList(String serviceName);
}
