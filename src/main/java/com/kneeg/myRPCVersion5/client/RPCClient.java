package com.kneeg.myRPCVersion5.client;


import com.kneeg.myRPCVersion5.common.RPCRequest;
import com.kneeg.myRPCVersion5.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest response);
}
