package com.kneeg.myRPCVersion4.client;


import com.kneeg.myRPCVersion4.common.RPCRequest;
import com.kneeg.myRPCVersion4.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest response);
}
