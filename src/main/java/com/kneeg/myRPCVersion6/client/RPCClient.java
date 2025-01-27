package com.kneeg.myRPCVersion6.client;


import com.kneeg.myRPCVersion6.common.RPCRequest;
import com.kneeg.myRPCVersion6.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest response);
}
