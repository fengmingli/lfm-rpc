package com.lifengming.rpc.common.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 *  服务持有对象，保存具体的服务信息备用
 *
 * @author lifengming
 * @date 2021.07.24
 */
@Getter
@Builder
@ToString
public class ServiceObject {
    /**
     *
     * 服务名称
     */
    private final String serviceName;

    /**
     * 服务Class
     */
    private final Class<?> clazz;

    /**
     * 具体服务
     */
    private final Object object;
}
