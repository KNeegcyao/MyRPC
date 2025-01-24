package com.kneeg.myRPCVersion3.client;


import com.kneeg.myRPCVersion3.common.RPCRequest;
import com.kneeg.myRPCVersion3.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest response);
}
