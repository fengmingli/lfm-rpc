package com.lifengming.rpc.register.zookeeper;

import com.lifengming.rpc.register.localcache.ServiceLocalCache;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

/**
 * 子节点事件监听处理类
 * @author lifengming
 * @date 2021.07.25
 */
@Slf4j
public class ZookeeperChildListenerImpl implements IZkChildListener {

    @Override
    public void handleChildChange(String parentPath, List<String> childList) throws Exception {
        log.debug("Child change parentPath:[{}] -- childList:[{}]", parentPath, childList);
        // 只要子节点有改动就清空缓存
        String[] arr = parentPath.split("/");
        ServiceLocalCache.removeAll(arr[2]);
    }
}
