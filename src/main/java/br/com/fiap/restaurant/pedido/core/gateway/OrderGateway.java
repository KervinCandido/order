package br.com.fiap.restaurant.pedido.core.gateway;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface OrderGateway {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
    Page<Order> findByUser(UUID userUuid, int pageNumber, int pageSize);
    Page<Order> findByUserAndStatus(UUID userUuid, Set<StatusOrder> orderStatus, int pageNumber, int pageSize);
}
