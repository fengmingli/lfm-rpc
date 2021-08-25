# lfm-rpc
为什么要开源这个lfm-rpc项目？
- lfm-rpc 是基于netty实现的rpc框架
- spi 机制的大量使用，功能高拓展
- 普通 Java 项目与 Spring 的整合
- maven-release-plugin、maven-checkstyle-plugin 等maven插件的使用 
  
lfm-rpc 项目可以很好的帮助开发同学对netty、SPI机制、项目与 Spring的整合，以及常用maven插件的学习与实践
## 快速开始
打开 rpc-samples/rpc-samples-springboot 目录
- step 0：运行准备
  - 需要在本机部署一个 zookeeper 服务，用户服务注册 默认：127.0.0.1:2181
  - 其他服务配置参考：com.lifengming.springboot.config.RpcConfig
- step 1：启动 rpc-sample-springboot-server
- step 2：启动 rpc-sample-springboot-client
- step 3：浏览器访问：http://127.0.0.1:8888/rpc/demo

## 基础组件
- protocol 公共协议
- client 客户端相关
- server 服务端相关
- serialization 序列化相关
- register 
  - 服务的注册与发现
  - 服务的负载均衡
  - 服务注册表本地缓存
- common

## 调用过程
1. client 会调用本地动态代理 proxy 
2. 这个代理会将调用通过协议转序列化字节流
3. 通过 netty 网络框架，将字节流发送到服务端
3. 服务端在受到这个字节流后，会根据协议，反序列化为原始地调用，利用反射原理调用服务方提供的方法
4. 如果请求有返回值，又需要把结果根据协议序列化后，再通过 netty 返回给调用方

## 项目发布流程
```
mvn release:prepare -Darguments="-DskipITs -DskipTestITs -DskipTests" // 更新版本号 打tag
mvn release:update-versions // 清理目录
git push // 提交
```
