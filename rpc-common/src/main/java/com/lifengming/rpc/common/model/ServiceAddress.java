package com.lifengming.rpc.common.model;

import lombok.*;

/**
 * @author lifengming
 * @since 2019.09.17
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceAddress {
    /**
     * 服务ip
     */
    private String ip;
    /**
     * 服务端口
     */
    private int port;

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
