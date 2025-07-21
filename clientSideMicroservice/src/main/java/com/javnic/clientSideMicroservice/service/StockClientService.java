package com.javnic.clientSideMicroservice.service;

import com.javnic.grpc.StockRequest;
import com.javnic.grpc.StockResponse;
import com.javnic.grpc.StockTradingServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceBlockingStub stockTradingServiceBlockingStub;

    public StockResponse getStockPrice(String stockSymbol){
        StockRequest request = StockRequest.newBuilder()
                .setStockSymbol(stockSymbol)
                .build();
        return stockTradingServiceBlockingStub.getStockPrice(request);
    }
}
