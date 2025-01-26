package com.kneeg.myRPCVersion5.client;


import com.kneeg.myRPCVersion5.common.RPCRequest;
import com.kneeg.myRPCVersion5.common.RPCResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {
    private RPCClient client;

    // jdk 动态代理， 每一次代理对象调用方法，会经过此方法增强（反射获取request对象，socket发送至客户端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return "Proxy for interface: " + method.getDeclaringClass().getName();
        }
        // request的构建，使用了lombok中的builder，代码简洁
        RPCRequest request = RPCRequest.builder().interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .params(args).paramsTypes(method.getParameterTypes()).build();
        //数据传输
        RPCResponse response = client.sendRequest(request);
        //System.out.println(response);
        if (response == null) {
            throw new NullPointerException("RPC response is null");
        }
        return response.getData();
    }
    public <T> T getProxy(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("The provided class must be an interface");
        }
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T) o;
    }
}