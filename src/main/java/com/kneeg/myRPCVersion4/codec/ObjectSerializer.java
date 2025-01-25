package com.kneeg.myRPCVersion4.codec;

import java.io.*;

public class ObjectSerializer implements Serializer{
    //利用java IO对象 ->字节数组
    @Override
    public byte[] serializer(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);  // 将对象写入输出流
            oos.flush();           // 强制刷新缓冲区
            bytes = bos.toByteArray(); // 转换为字节数组
            oos.close();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();  // 仅打印异常，未处理
        }
        return bytes;
    }

    //字节数组 -> 对象
    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();  // 从字节数组读取对象
            ois.close();
            bis.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();  // 仅打印异常，未处理
        }
        return obj;
    }

    //0代表java原生序列化器
    @Override
    public int getType() {
        return 0;
    }
}
