package br.com.fiap.restaurant.pedido.infra.controller.response;

import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        Long id,
        UUID customerUuid,
        LocalDateTime orderDateTime,
        StatusOrder status,
        List<OrderItemResponse> items,
        BigDecimal total
) {
    public OrderResponse(OrderOutput order) {
        this(order.id(), order.customerUuid(), order.orderDateTime(), order.status(),
                order.items().stream().map(OrderItemResponse::new).toList(), order.total());
    }
}
