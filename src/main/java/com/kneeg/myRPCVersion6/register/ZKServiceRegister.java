package com.kneeg.myRPCVersion6.register;

import com.kneeg.myRPCVersion6.loadbalance.LoadBalance;
import com.kneeg.myRPCVersion6.loadbalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * zookeeper服务注册接口的实现类
 */
public class ZKServiceRegister implements ServiceRegister {
    //curator提供的zookeeper客户端
    private CuratorFramework client;
    //zookeeper根路径节点
    private static final String ROOT_PATH="MyRPC";
    // 初始化负载均衡器， 这里用的是随机， 一般通过构造函数传入
    private LoadBalance loadBalance = new RandomLoadBalance();

    //这里负责zookeeper客户端的初始化，并于zookeeper服务端建立连接
    public ZKServiceRegister(){
        //定义客户端连接失败时的指数退避重试策略，指定时间重试
        ExponentialBackoffRetry policy = new ExponentialBackoffRetry(1000, 3);//1000：初始重试等待时间（毫秒）,3：最大重试次数
        //zookeeper的地址固定，不管是服务提供者，还是消费者都要与之建立连接
        //sessionTimeoutMs与zoo.cfg中的tickTime有关系
        //zk还会根据minSessionTimeout与maxSessionTimeout两个参数重新调整最后的超时值。默认分别为tickTime的2倍和20倍
        //使用心跳监听状态
        this.client= CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181") // ZooKeeper服务器地址
                .sessionTimeoutMs(40000)         // 会话超时时间（40秒）
                .retryPolicy(policy)             // 使用上述重试策略
                .namespace(ROOT_PATH)            // 根命名空间（隔离不同应用的节点）
                .build();
        this.client.start();
        System.out.println("zookeeper  连接成功");
    }
    @Override
    public void register(String serviceName, InetSocketAddress serverAddress) {
        try {
            String servicePath = "/" + serviceName;
            // 1. 创建持久化服务节点（如果不存在）
            if (client.checkExists().forPath(servicePath) == null) {
                client.create()
                        .creatingParentsIfNeeded() // 自动创建父节点（如 ROOT_PATH）
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(servicePath);
            }

            // 2. 创建临时节点存储服务地址
            String path = "/" + serviceName + "/" + getServiceAddress(serverAddress);
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL) // 临时节点
                    .forPath(path);
        } catch (Exception e) {
            System.out.println("此服务已存在");
        }
    }

    /**
     * 根据服务名返回地址
     * @param serviceName
     * @return
     */
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            String servicePath = "/" + serviceName;
            // 1. 获取服务下所有地址节点
            List<String> addresses = client.getChildren().forPath(servicePath);

            //负载均衡选择器，选择一个
            String balance = loadBalance.balance(addresses);
            return parseAddress(balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //地址序列化为字符串 地址->XXX.XXX.XXX.XXX:port 字符串
    private String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName()+
                ":"+
                serverAddress.getPort();
    }

    // 字符串反序列化为地址 字符串解析为地址
    private InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0],Integer.parseInt(result[1]));
    }
}
