package com.lifengming.rpc.register.servicecenter;

import com.lfm.rpc.core.model.ServiceAddress;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lifengming
 * @since 2019.10.24
 */
public class LocalHost {
    private static volatile LocalHost LOCALHOST = null;

    private LocalHost() {
    }

    public static LocalHost getLocalHost() {
        if (LOCALHOST == null) {
            synchronized (LocalHost.class) {
                if (LOCALHOST == null) {
                    LOCALHOST = new LocalHost();
                }
            }
        }
        return LOCALHOST;
    }

    /**
     * 服务注册放到内存中
     **/
    private ConcurrentHashMap<String, ServiceAddress> services = new ConcurrentHashMap<>();

    public ServiceAddress getServiceAddressByService(String serviceName) {
        return services.get(serviceName);
    }

    public void serviceRegister(String serviceName, ServiceAddress serviceAddress) {
        this.services.put(serviceName, serviceAddress);
    }

}
