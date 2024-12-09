package com.kneeg.myRPCVersion0.server;

import com.kneeg.myRPCVersion0.common.User;
import com.kneeg.myRPCVersion0.service.Impl.UserserviceImpl;
import com.kneeg.myRPCVersion0.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
                        //读取客户端传过来的id
                        Integer id = ois.readInt();
                        User user = userService.getById(id);
                        //写入user对象给客户端
                        oos.writeObject(user);
                        oos.flush();
                    }catch (IOException e){
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
