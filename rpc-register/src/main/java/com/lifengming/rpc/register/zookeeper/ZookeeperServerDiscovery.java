package com.lifengming.rpc.register.zookeeper;

import cn.hutool.json.JSONUtil;
import com.lifengming.rpc.common.constans.RpcConstant;
import com.lifengming.rpc.common.model.Service;
import com.lifengming.rpc.register.ServerDiscovery;
import com.lifengming.rpc.register.localcache.ServiceLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基于ZK的服务发现
 * @author lifengming
 * @date 2021.07.25
 */
@Slf4j
public class ZookeeperServerDiscovery implements ServerDiscovery {

    private final ZkClient zkClient;

    public ZookeeperServerDiscovery(String zkAddress) {
        zkClient = new ZkClient(zkAddress);
        zkClient.setZkSerializer(new MyZookeeperSerializer());
    }


    /**
     * zk 文件目录：/rpc/${serviceName}/service
     *
     * @param serviceName 服务名
     * @return 服务列表
     */
    @Override
    public List<Service> getServiceList(String serviceName) {
        String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + serviceName + "/service";
        List<String> children = zkClient.getChildren(servicePath);
        return Optional.ofNullable(children)
                .orElse(new ArrayList<>())
                .stream()
                .map(str -> {
                    String deCh = null;
                    try {
                        deCh = URLDecoder.decode(str, RpcConstant.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return JSONUtil.toBean(deCh, Service.class);
                }).collect(Collectors.toList());
    }

    @Override
    public void watchChildNode(ServerDiscovery serverDiscovery) {
        // 注册子节点监听
        if (serverDiscovery instanceof ZookeeperServerDiscovery) {
            ServiceLocalCache.SERVICE_CLASS_NAMES.forEach(name -> {
                String servicePath = RpcConstant.ZK_SERVICE_PATH + RpcConstant.PATH_DELIMITER + name + "/service";
                zkClient.subscribeChildChanges(servicePath, new ZookeeperChildListenerImpl());
            });
            log.info("subscribe service zk node successfully");
        }
    }
}
