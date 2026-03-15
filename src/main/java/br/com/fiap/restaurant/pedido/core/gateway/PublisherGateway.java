package br.com.fiap.restaurant.pedido.core.gateway;

import java.util.concurrent.CompletableFuture;

public interface PublisherGateway<E> {
    CompletableFuture<Void> publish(E event);
}
