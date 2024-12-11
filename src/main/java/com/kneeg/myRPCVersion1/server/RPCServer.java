package com.kneeg.myRPCVersion1.server;

import com.kneeg.myRPCVersion1.common.RPCReponse;
import com.kneeg.myRPCVersion1.common.RPCRequest;
import com.kneeg.myRPCVersion1.common.User;
import com.kneeg.myRPCVersion1.service.Impl.UserserviceImpl;
import com.kneeg.myRPCVersion1.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

public class RPCServer {
    public static void main(String[] args) {
        UserService userService = new UserserviceImpl();
        try{
            ServerSocket serverSocket = new ServerSocket(8899);
            System.out.println("服务端启动了");
            //BIO的方式监听Socket
            while(true){
                Socket socket = serverSocket.accept();
                new Thread(()->{
                    try{
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        //读取客户端传过来的request
                        RPCRequest request =(RPCRequest)ois.readObject();
                        //反射调用对应方法
                        //userService.getClass(): 获取 userService 的运行时 Class 对象，表示 userService 的类型信息。
                        //request.getMethodName(): 从客户端的请求中获取方法名（例如 "findUserById"）。
                        //request.getParamsTypes(): 获取参数类型数组，用于匹配目标方法的参数签名。
                        //参数类型是一个 Class[] 数组，例如 [String.class, int.class]。
                        //这是为了精确定位目标方法（方法重载可能存在多个同名方法）。
                        //getMethod(String name, Class<?>... parameterTypes): 反射方法，查找类中与指定方法名和参数类型匹配的方法。
                        Method method = userService.getClass()
                                .getMethod(request.getMethodName(), request.getParamsTypes());
                        //method.invoke(Object obj, Object... args):
                        //调用 method 所表示的方法。
                        //obj: 调用该方法的对象，这里是 userService。
                        //args: 方法参数的值数组（例如 ["123", 25]）。
                        //返回值:
                        //如果方法有返回值，invoke 就是方法的执行结果。
                        //如果方法返回 void，则 invoke 为 null。
                        Object invoke = method.invoke(userService, request.getParams());
                        //封装，写入reponse对象
                        oos.writeObject(RPCReponse.success(invoke));
                        oos.flush();

                    }catch (IOException | ClassNotFoundException
                            |NoSuchMethodException|IllegalAccessException
                            | InvocationTargetException e){
                        e.printStackTrace();
                        System.out.println("从IO中读取数据错误");
                    }
                }).start();
            }
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("服务器启动失败");
        }
    }
}
