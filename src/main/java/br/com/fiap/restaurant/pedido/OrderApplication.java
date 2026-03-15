package br.com.fiap.restaurant.pedido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderApplication {

    private OrderApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}