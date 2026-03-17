package br.com.fiap.restaurant.pedido.infra.controller.response;

import br.com.fiap.restaurant.pedido.core.outbound.OrderItemOutput;

import java.math.BigDecimal;

public record OrderItemResponse (
        Long menuItemId,
        String name,
        BigDecimal quantity,
        BigDecimal price,
        BigDecimal total
) {
    public OrderItemResponse(OrderItemOutput orderItemOutput) {
        this (
            orderItemOutput.menuItemId(),
            orderItemOutput.name(),
            orderItemOutput.quantity(),
            orderItemOutput.unitPrice(),
            orderItemOutput.total()
        );
    }
}
