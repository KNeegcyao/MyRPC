package com.kneeg.myRPCVersion5.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kneeg.myRPCVersion5.common.RPCRequest;
import com.kneeg.myRPCVersion5.common.RPCResponse;

/**
 * 由于json序列化的方式是通过把对象转化成字符串,丢失了Data对象的类信息,所以deserialize需要
 * 了解对象的类信息，根据类信息把JsonObject -> 对应的对象
 */
public class JsonSerializer implements Serializer {
    @Override
    public byte[] serializer(Object obj) {
        byte[] bytes = JSONObject.toJSONBytes(obj);
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, int messageType) {
        Object obj=null;
        //传输的信息分为request和response
        switch (messageType){
            case 0: //处理RPCRequest
                RPCRequest request = JSON.parseObject(bytes, RPCRequest.class);
                //转换请求参数类型
                Object[] objects = new Object[request.getParams().length];
                for (int i = 0; i < objects.length; i++) {
                    Class<?> paramsType = request.getParamsTypes()[i];
                    if(!paramsType.isAssignableFrom(request.getParams()[i].getClass())){
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i], paramsType);
                    }else{
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj=request;
                break;
            case 1: //处理RPCResponse
                RPCResponse response = JSON.parseObject(bytes, RPCResponse.class);
                Class<?> dataType = response.getDataType();
                if(!dataType.isAssignableFrom(response.getData().getClass())){
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(),dataType));
                }
                obj=response;
                break;
            default:
                System.out.println("暂时不支持此种消息");
                throw new RuntimeException();
        }
        return obj;
    }

    //1 代表着json序列化方式
    @Override
    public int getType() {
        return 1;
    }
}
