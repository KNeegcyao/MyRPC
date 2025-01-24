package com.kneeg.myRPCVersion3.client;

import com.kneeg.myRPCVersion3.common.RPCRequest;
import com.kneeg.myRPCVersion3.common.RPCResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class RPCClientProxy implements InvocationHandler {
    //传入参数Service接口的class对象，反射封装为一个request
    private RPCClient client;
    //jdk动态代理，每一次代理对象调用方法，会经过此方法增强（反射获取request对象，socket发送至客户端）
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 处理 Object 类的方法（toString, hashCode, equals）
        if (method.getDeclaringClass() == Object.class) {
            return handleObjectMethod(proxy, method, args);
        }
        //request的构建
        RPCRequest request = new RPCRequest(method.getDeclaringClass().getName(),
                method.getName(),args, method.getParameterTypes());
        //数据传输
        RPCResponse response = client.sendRequest(request);
        return response.getData();
    }

    /**
     * 处理本地 Object 方法
     */
    private Object handleObjectMethod(Object proxy, Method method, Object[] args) {
        String methodName = method.getName();
        switch (methodName) {
            case "toString":
                return proxy.getClass().getName() + "@" + Integer.toHexString(proxy.hashCode());
            case "hashCode":
                return System.identityHashCode(proxy);
            case "equals":
                if (args == null || args.length != 1) {
                    throw new IllegalArgumentException("equals 方法参数错误");
                }
                return proxy == args[0];
            default:
                throw new UnsupportedOperationException("不支持的方法: " + method);
        }
    }

    <T>T getProxy(Class<T> clazz){
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        return (T)o;
    }
}
