package br.com.fiap.restaurant.pedido.core.gateway;

import br.com.fiap.restaurant.pedido.core.domain.Order;

import java.util.Optional;

public interface OrderGateway {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
}
