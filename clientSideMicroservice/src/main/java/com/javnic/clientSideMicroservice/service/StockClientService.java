package com.javnic.clientSideMicroservice.service;

import com.javnic.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class StockClientService {

    @GrpcClient("stockService")
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceStub;

//    public StockResponse getStockPrice(String stockSymbol){
//        StockRequest request = StockRequest.newBuilder()
//                .setStockSymbol(stockSymbol)
//                .build();
//        return stockTradingServiceBlockingStub.getStockPrice(request);
//    }

    public void subscribeStockPrice(String symbol){
        StockRequest request = StockRequest.newBuilder()
                .setStockSymbol(symbol)
                .build();
        stockTradingServiceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {

            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println("Stock Price Update: " + stockResponse.getStockSymbol() +
                        " Price: " + stockResponse.getPrice() + " " +
                        " Time: " + stockResponse.getTimestamp());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock price stream live update completed!");
            }
        });
    }

    public void placeBulkOrders(){

        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("Order summary received from server: ");
                System.out.println("Total orders: " + orderSummary.getTotalOrders());
                System.out.println("Successful Orders: " + orderSummary.getSuccessCount());
                System.out.println("Total amount: " + orderSummary.getTotalAmount());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Order summary receive error from server: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed, Server is done sending summary! ");
            }
        };
        StreamObserver<StockOrder> requestObserver = stockTradingServiceStub.bulkStockOrder(responseObserver);
        try{
            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("1")
                    .setStockSymbol("AAPL")
                    .setOrderType("BUY")
                    .setPrice(150.5)
                    .setQuantity(10)
                    .build()
            );
            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("2")
                    .setStockSymbol("GOOGLE")
                    .setOrderType("SELL")
                    .setPrice(1500.5)
                    .setQuantity(4)
                    .build()
            );
            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("3")
                    .setStockSymbol("META")
                    .setOrderType("BUY")
                    .setPrice(10.5)
                    .setQuantity(20)
                    .build()
            );
            requestObserver.onNext(StockOrder.newBuilder()
                    .setOrderId("4")
                    .setStockSymbol("QUANTA")
                    .setOrderType("SELL")
                    .setPrice(1500.5)
                    .setQuantity(1)
                    .build()
            );
            requestObserver.onCompleted();
        } catch (Exception e) {
            requestObserver.onError(e);
        }
    }

    public void startTrading() throws InterruptedException {
        StreamObserver<StockOrder> requestObserver = stockTradingServiceStub.liveTrading(new StreamObserver<TradeStatus>() {
            @Override
            public void onNext(TradeStatus tradeStatus) {
                System.out.println("Server response : " + tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed! ");
            }
        });

        for (int i = 1; i <= 10; i++) {
            StockOrder stockOrder = StockOrder.newBuilder()
                    .setOrderId("ORDER-"+i)
                    .setStockSymbol("APPL-"+i)
                    .setQuantity(i*10)
                    .setPrice(150.0+i)
                    .setOrderType("BUY")
                    .build();
            requestObserver.onNext(stockOrder);
            Thread.sleep(2000);
        }
        requestObserver.onCompleted();
    }
}
