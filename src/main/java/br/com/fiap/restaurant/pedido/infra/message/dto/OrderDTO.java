package br.com.fiap.restaurant.pedido.infra.message.dto;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDTO (
        Long id,
        Long restaurantId,
        UUID customerUuid,
        List<OrderItemDTO> items,
        LocalDateTime orderDateTime,
        StatusOrder status) {
    public OrderDTO(Order order) {
        this(order.getId(),
            order.getRestaurantId(),
            order.getCustomerUuid(), order.getItems().stream().map(OrderItemDTO::new).toList(),
            order.getOrderDateTime(),
            order.getStatus());
    }
}
