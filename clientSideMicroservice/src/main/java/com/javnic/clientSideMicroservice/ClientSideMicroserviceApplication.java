package com.javnic.clientSideMicroservice;

import com.javnic.clientSideMicroservice.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ClientSideMicroserviceApplication implements CommandLineRunner {

	private StockClientService stockClientService;

	public ClientSideMicroserviceApplication(StockClientService stockClientService) {
		this.stockClientService = stockClientService;
	}
	public static void main(String[] args) {
		SpringApplication.run(ClientSideMicroserviceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		System.out.println("Grpc client response : " + stockClientService.getStockPrice("GOOGLE"));
//		stockClientService.subscribeStockPrice("GOOGLE");
//		stockClientService.placeBulkOrders();
		stockClientService.startTrading();
	}
}
