package com.kneeg.myRPCVersion6.loadbalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡
 */
public class RandomLoadBalance implements LoadBalance{
    private static final Logger logger = LoggerFactory.getLogger(RandomLoadBalance.class);

    @Override
    public String balance(List<String> addressList) {
        if (addressList.isEmpty()) {
            logger.warn("⚠️ 无可用服务节点");
            throw new IllegalArgumentException("No available service");
        }
        Random random = new Random();
        int choose = random.nextInt(addressList.size());
        logger.info("🎯 负载均衡选择 | 节点: {} | 索引: {}", addressList.get(choose), choose);
        return addressList.get(choose);
    }
}
