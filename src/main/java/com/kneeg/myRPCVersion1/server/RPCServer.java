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
                        Method method = userService.getClass()
                                .getMethod(request.getMethodName(), request.getParamsTypes());
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
