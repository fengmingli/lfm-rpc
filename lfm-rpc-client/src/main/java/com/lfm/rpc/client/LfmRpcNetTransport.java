package com.lfm.rpc.client;

import com.lfm.rpc.core.model.RpcRequest;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author lifengming
 * @since 16.09.2019
 */

@AllArgsConstructor
public class LfmRpcNetTransport {

    String host;
    int port;

    private Socket newSocket() {
        System.out.println("创建一个socket连接");
        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            throw new RuntimeException("连接失败！");
        }
        return socket;
    }

    public Object sendRequest(RpcRequest rpcRequest) {
        Socket socket = null;
        try {
            socket = newSocket();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object result = objectInputStream.readObject();
            objectInputStream.close();
            objectOutputStream.close();
            return result;
        } catch (Exception e) {
            throw new RuntimeException("发送数据异常：" + e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
