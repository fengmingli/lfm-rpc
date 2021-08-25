package com.lifengming.rpc.register.zookeeper;

import cn.hutool.json.JSONUtil;
import com.lifengming.rpc.common.constans.RpcConstant;
import com.lifengming.rpc.common.exception.RpcException;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.common.model.ServiceObject;
import com.lifengming.rpc.common.util.NetUtils;
import com.lifengming.rpc.register.DefaultServerRegister;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * zk服务注册器，提供服务注册、暴露服务的能力
 *
 * @author lifengming
 * @date 2021.07.24
 */
public class ZookeeperServerRegister extends DefaultServerRegister {

    private final ZkClient zookeeperClient;

    public ZookeeperServerRegister(String zkAddress, Integer port, String protocol, Integer weight) {
        zookeeperClient = new ZkClient(zkAddress);
        zookeeperClient.setZkSerializer(new MyZookeeperSerializer());
        this.port = port;
        this.protocol = protocol;
        this.weight = weight;
    }

    @Override
    public void serviceRegister(ServiceObject serviceObject) throws RpcException {
        super.serviceRegister(serviceObject);

        String host = NetUtils.getLocalAddress().getHostAddress();
        String address = host + ":" + port;

        Service service = Service.builder()
                .address(address)
                .name(serviceObject.getClazz().getName())
                .protocol(protocol)
                .weight(this.weight)
                .build();

        this.exportService(service);
    }


    @Override
    public ServiceObject getServiceObjByServiceName(String serviceName) throws RpcException {
        return super.getServiceObjByServiceName(serviceName);
    }

    /**
     * 服务暴露(其实就是把服务信息保存到Zookeeper上)
     *
     * @param serviceResource 服务资源
     */
    private void exportService(Service serviceResource) {
        String serviceName = serviceResource.getName();
        String uri = JSONUtil.toJsonStr(serviceResource);

        try {
            uri = URLEncoder.encode(uri, RpcConstant.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + serviceName + "/service";
        if (!zookeeperClient.exists(servicePath)) {
            // 没有该节点就创建
            zookeeperClient.createPersistent(servicePath, true);
        }

        String uriPath = servicePath + RpcConstant.PATH_DELIMITER + uri;
        if (zookeeperClient.exists(uriPath)) {
            // 删除之前的节点
            zookeeperClient.delete(uriPath);
        }
        // 创建一个临时节点，会话失效即被清理
        zookeeperClient.createEphemeral(uriPath);
    }
}
