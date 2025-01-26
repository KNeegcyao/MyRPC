# ObjectSerializer
## 1. 这个代码是干什么的？
   想象你要把一个 玩具 放进快递盒寄给朋友：

- 序列化：把玩具拆开，按说明书打包成箱子里的零件（对象 → 字节数组）。

- 反序列化：朋友收到箱子后，按说明书把零件重新组装成玩具（字节数组 → 对象）。

这个 ObjectSerializer 类就是帮你完成打包和拆箱的工具。

## 2. 代码逐句详解
   打包过程（序列化）
   ```java
public byte[] serializer(Object obj) {
        byte[] bytes=null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); // 1. 拿一个空箱子（内存缓冲区）
        try {
           ObjectOutputStream oos = new ObjectOutputStream(bos); // 2. 准备装箱说明书（对象输出流）
           oos.writeObject(obj); // 3. 按说明书把玩具拆开放进箱子
           oos.flush(); // 4. 压紧箱子确保不留空隙
           bytes = bos.toByteArray(); // 5. 封箱，得到一个标准的快递箱（字节数组）
           oos.close();
           bos.close(); // 6. 把工具收起来（关流）
        }catch (IOException e){
           e.printStackTrace(); // 万一拆坏了，记录问题（但没处理）
        }
        return bytes; // 7. 把快递箱交给快递员
}
   ```
   拆箱过程（反序列化）
   ```java
   public Object deserialize(byte[] bytes, int messageType) {
     Object obj=null;
     ByteArrayInputStream bis = new ByteArrayInputStream(bytes); // 1. 拿到快递箱
     try {
         ObjectInputStream ois = new ObjectInputStream(bis); // 2. 准备拆箱说明书（对象输入流）
         obj=ois.readObject(); // 3. 按说明书把零件组装成玩具
         ois.close();
         bis.close(); // 4. 收拾工具（关流）
     }catch (IOException | ClassNotFoundException e){
         e.printStackTrace(); // 如果零件缺失或说明书不对，记录问题（但没处理）
   }
   return obj; // 5. 把组装好的玩具交给朋友
}
```
-----

# JSONSerializer

## 示例场景
假设服务端方法定义为：

```java
public User getUserById(Integer userId, boolean isActive);
```
客户端发送的请求参数可能为：
```json
{
  "params": [
    1001,                     // Integer -> 直接使用
    {"isActive": true}        // JSONObject -> 需转换为 boolean
  ],
  "paramsTypes": [
    "java.lang.Integer",
    "boolean"
  ]
}
```
代码处理过程：

1.第一个参数（1001）

  - 目标类型：Integer.class

  - 实际类型：Integer

  - 直接使用，无需转换。

2.第二个参数（{"isActive": true}）

  - 目标类型：boolean.class

  - 实际类型：JSONObject

  - 转换：将 JSONObject 转换为 boolean（实际应为 Boolean
--------
# MyDecode和MyEncode
这两个类是基于 Netty 的编解码器，用于定义 自定义消息协议，解决 TCP 粘包/半包问题，并完成对象与字节流的相互转换：

  - MyEncode（编码器）：将对象（RPCRequest 或 RPCResponse）序列化为字节流，按自定义协议格式写入缓冲区。

  - MyDecode（解码器）：从字节流中按协议格式解析出原始对象，传递给后续处理器。

## 自定义消息协议格式
消息格式由四部分组成（总长度 = 2 + 2 + 4 + N 字节）：

1.消息类型（2 字节）：标识是请求（REQUEST）还是响应（RESPONSE）。

2.序列化类型（2 字节）：标识使用的序列化器（如 JSON、Protobuf）。

3.数据长度（4 字节）：记录序列化后的字节数组长度。

4.数据内容（N 字节）：对象的序列化字节数组。
```
+----------------+----------------+----------------+----------------+
|  消息类型 (2)  | 序列化类型 (2) | 数据长度 (4)   | 数据内容 (N)    |
+----------------+----------------+----------------+----------------+
```
```java
@AllArgsConstructor
public class MyEncode extends MessageToByteEncoder {
    private Serializer serializer; // 序列化器（如JSON、Protobuf）

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        // 1. 写入消息类型（2字节）
        if (msg instanceof RPCRequest) {
            out.writeShort(MessageType.REQUEST.getCode());
        } else if (msg instanceof RPCResponse) {
            out.writeShort(MessageType.RESPONSE.getCode());
        }

        // 2. 写入序列化类型（2字节）
        out.writeShort(serializer.getType());

        // 3. 序列化对象为字节数组
        byte[] serialize = serializer.serialize(msg);

        // 4. 写入数据长度（4字节）
        out.writeInt(serialize.length);

        // 5. 写入数据内容（N字节）
        out.writeBytes(serialize);
    }
}
```
**关键步骤：**

1.消息类型：通过 instanceof 判断对象类型，写入对应的标识码（如 REQUEST=0）。

2.序列化类型：写入当前序列化器的类型码（如 JSON=1）。

3.数据序列化：调用 serializer.serialize 将对象转为字节数组。

4.数据长度：写入字节数组长度，用于后续解码时准确读取内容。

5.数据内容：将序列化后的字节数组写入缓冲区。

```java
@AllArgsConstructor
public class MyDecode extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 1. 读取消息类型（2字节）
        short messageType = in.readShort();
        if (!isValidType(messageType)) { // 验证消息类型
            System.out.println("暂不支持此种数据");
            return;
        }

        // 2. 读取序列化类型（2字节）
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByCode(serializerType);
        if (serializer == null) { // 检查序列化器是否存在
            throw new RuntimeException("不存在对应的序列化器");
        }

        // 3. 读取数据长度（4字节）
        int length = in.readInt();

        // 4. 读取数据内容（N字节）
        byte[] bytes = new byte[length];
        in.readBytes(bytes);

        // 5. 反序列化为对象
        Object deserialize = serializer.deserialize(bytes, messageType);
        out.add(deserialize);
    }

    private boolean isValidType(short messageType) {
        return messageType == MessageType.REQUEST.getCode() || 
               messageType == MessageType.RESPONSE.getCode();
    }
}
```
**关键步骤：**

1.消息类型：读取前 2 字节，验证是否为支持的请求/响应类型。

2.序列化类型：根据类型码获取对应的序列化器（如 JSON）。

3.数据长度：读取后续 4 字节，确定需要读取的字节数组长度。

4.数据内容：根据长度读取字节数组。

5.反序列化：调用 serializer.deserialize 将字节数组还原为对象。