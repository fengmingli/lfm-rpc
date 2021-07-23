# lfm-rpc

## 项目构建
```
mvn release:prepare -Darguments="-DskipITs -DskipTestITs -DskipTests" // 更新版本号 打tag
mvn release:update-versions // 清理目录
git push // 提交
```


## 基础组件
- protocol

## 调用过程
1. client 会调用本地动态代理 proxy 
2. 这个代理会将调用通过协议转序列化字节流
3. 通过 netty 网络框架，将字节流发送到服务端
3. 服务端在受到这个字节流后，会根据协议，反序列化为原始的调用，利用反射原理调用服务方提供的方法
4. 如果请求有返回值，又需要把结果根据协议序列化后，再通过 netty 返回给调用方
