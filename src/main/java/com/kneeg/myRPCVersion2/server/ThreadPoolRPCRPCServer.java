package com.kneeg.myRPCVersion2.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 这个实现类代表着java原始的BIO监听模式，来一个任务，就new一个线程去处理
 * 处理任务的工作见WorkThread中
 */
public class ThreadPoolRPCRPCServer implements RPCServer{
    private ServiceProvider serviceProvide;
    private final ThreadPoolExecutor threadPool;

    //线程池
    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvide) {
        this.serviceProvide = serviceProvide;
        threadPool = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),//核心线程数
                1000, //最大线程数
                60, TimeUnit.SECONDS,//空闲线程存活时间
                new ArrayBlockingQueue<>(100)//阻塞对列
        );
    }
    public ThreadPoolRPCRPCServer(ServiceProvider serviceProvide, int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  BlockingQueue<Runnable> workQueue){
        threadPool=new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue);
        this.serviceProvide=serviceProvide;
    }
    @Override
    public void start(int port) {
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("服务端启动了...");
            //BIO的方式监听Socket
            while(true){
                Socket socket = serverSocket.accept();
                //开启一个新线程去处理
                threadPool.execute(new WorkThread(socket,serviceProvide));
            }
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {
        System.out.println("服务端已停止");
    }
}
