package com.kneeg.myRPCVersion2.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 之前这里使用Map简单实现的
 * 存放服务接口名与服务端对应的实现类
 * 服务启动时要暴露其相关的实现类0
 * 根据request中的interface调用服务端中相关实现类
 */
public class ServiceProvider {
    /**
     * 一个实现类可能实现多个接口
     */
    // 服务接口名 -> 服务实现类
    private Map<String, Object> interfaceProvider;

    public ServiceProvider(){
        this.interfaceProvider = new HashMap<>();
    }

    /**
     * 注册服务实现类及其所有接口
     * @param service
     */
    public void provideServiceInterface(Object service){
        String serviceName = service.getClass().getName();
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for(Class clazz : interfaces){
            interfaceProvider.put(clazz.getName(),service);
        }

    }

    /**
     * 根据接口名获取服务实现类
     */
    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }
}