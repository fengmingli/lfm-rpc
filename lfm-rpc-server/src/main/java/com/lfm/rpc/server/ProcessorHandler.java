package com.lfm.rpc.server;



import com.lfm.rpc.core.model.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 *<p>处理socket</>
 * @author lifengming
 * @since 16.09.2019
 */
public class ProcessorHandler implements Runnable {
    Socket socket;
    Object service;

    public ProcessorHandler(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }

    public ProcessorHandler() {
    }

    @Override
    public void run() {
        System.out.println("开始处理客户端请求");
        ObjectInputStream inputStream = null;
        try {

            inputStream = new ObjectInputStream(socket.getInputStream());
            //java的反序列化
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            Object result = this.invoke(rpcRequest);

            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object invoke(RpcRequest rpcRequest) {
        Object[] args = rpcRequest.getParameters();
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), types);
            Object result = method.invoke(service, args);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
