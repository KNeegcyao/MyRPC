package com.kneeg.myRPCVersion2.client;

import com.kneeg.myRPCVersion2.common.RPCResponse;
import com.kneeg.myRPCVersion2.common.RPCRequest;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    //传入参数Service接口的class对象，反射封装为一个request
    private String host;
    private int port;

    //jdk动态代理，每一次代理对象调用方法，会经过此方法增强（反射获取request对象，socket发送至客户端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        //request的构建
        RPCRequest request = new RPCRequest(method.getDeclaringClass().getName(),
                method.getName(),args, method.getParameterTypes());
        //数据传输
        RPCResponse response = IOClient.sendRequest(host, port, request);
        return response.getData();
    }
    <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
