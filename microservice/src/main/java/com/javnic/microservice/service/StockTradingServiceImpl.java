package com.javnic.microservice.service;

import com.javnic.grpc.*;
import com.javnic.microservice.entity.Stock;
import com.javnic.microservice.repository.StockRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    private final StockRepository stockRepository;

    public StockTradingServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {

        // stockName -> DB -> map response -> return

        String  stockSymbol = request.getStockSymbol();
        Stock stockEntity = stockRepository.findByStockSymbol(stockSymbol);

        StockResponse stockResponse = StockResponse.newBuilder()
                .setStockSymbol(stockEntity.getStockSymbol())
                .setPrice(stockEntity.getPrice())
                .setTimestamp(stockEntity.getLastUpdated().toString())
                .build();

        responseObserver.onNext(stockResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void subscribeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        String symbol = request.getStockSymbol();
        try{
            for (int i = 0; i <= 10 ; i++) {
                StockResponse stockResponse = StockResponse.newBuilder()
                        .setStockSymbol(symbol)
                        .setPrice(new Random().nextDouble(200))
                        .setTimestamp(Instant.now().toString())
                        .build();
                responseObserver.onNext(stockResponse);
                TimeUnit.SECONDS.sleep(1);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {

        return new StreamObserver<StockOrder>() {

            private int totalOrder=0;
            private double totalAmount=0;
            private int successCount=0;

            @Override
            public void onNext(StockOrder stockOrder) {
                totalOrder++;
                totalAmount+=stockOrder.getPrice()* stockOrder.getQuantity();
                successCount++;
                System.out.println("Received order : " + stockOrder);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Server unable to process the request : " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                OrderSummary summary = OrderSummary.newBuilder()
                        .setTotalOrders(totalOrder)
                        .setSuccessCount(successCount)
                        .setTotalAmount(totalAmount)
                        .build();

                responseObserver.onNext(summary);
                responseObserver.onCompleted();

            }
        };
    }

    @Override
    public StreamObserver<StockOrder> liveTrading(StreamObserver<TradeStatus> responseObserver) {
        return new StreamObserver<StockOrder>() {
            @Override
            public void onNext(StockOrder stockOrder) {
                System.out.println("Received order: " + stockOrder);
                String status="EXECUTED";
                String message="Order placed successfully.";
                if(stockOrder.getQuantity() <= 0){
                    status="FAILED";
                    message="Invalid quantity";
                }

                TradeStatus tradeStatus = TradeStatus.newBuilder()
                        .setOrderId(stockOrder.getOrderId())
                        .setMessage(message)
                        .setStatus(status)
                        .setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                        .build();
                responseObserver.onNext(tradeStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error :  " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
