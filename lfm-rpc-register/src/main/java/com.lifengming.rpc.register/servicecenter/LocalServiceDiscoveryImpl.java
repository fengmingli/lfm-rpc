package com.lifengming.rpc.register.servicecenter;

import com.lfm.rpc.core.model.ServiceAddress;

import static com.lifengming.rpc.register.servicecenter.LocalHost.getLocalHost;

/**
 * @author lifengming
 * @since 2019.10.23
 */
public class LocalServiceDiscoveryImpl implements ServiceDiscovery {
    @Override
    public String serviceDiscovery(String serviceName) {
        StringBuffer stringBuffer = new StringBuffer();
        ServiceAddress serviceAddress = getLocalHost().getServiceAddressByService(serviceName);
        if (serviceAddress == null) {
            return null;
        }
        String ip = serviceAddress.getIp();
        int host = serviceAddress.getPort();
        stringBuffer.append(ip);
        stringBuffer.append(",");
        stringBuffer.append(host);
        return stringBuffer.toString();
    }
}
