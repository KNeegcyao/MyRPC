package com.kneeg.myRPCVersion3.common;

import lombok.Data;

import java.io.Serializable;
/**
 * 上个例子中response传输的是User对象，显然在一个应用中我们不可能只传输一种类型的数据
 * 由此我们将传输对象抽象成为Object
 * Rpc需要经过网络传输，有可能失败，类似于http，引入状态码和状态信息表示服务调用成功还是失败
 */
@Data
public class RPCResponse implements Serializable {
  //状态信息
  private int code;
  private String message;
  //具体数据
  private  Object data;

    public RPCResponse(int i, String message) {
        this.code=i;
        this.message=message;
    }

    public RPCResponse(int i,Object data){
        this.code=i;
        this.data=data;
    }
    public static RPCResponse success(Object data){
        return new RPCResponse(200, data);
    }
    public static RPCResponse fail(){
        return new RPCResponse(500,"服务器发生错误");
    }
}
