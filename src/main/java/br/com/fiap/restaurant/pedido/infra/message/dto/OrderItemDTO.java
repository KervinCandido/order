package br.com.fiap.restaurant.pedido.infra.message.dto;

import br.com.fiap.restaurant.pedido.core.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemDTO(Long menuItemId, String menuItemName, BigDecimal quantity, BigDecimal unitPrice) {
    public OrderItemDTO(OrderItem orderItem) {
        this(orderItem.getMenuItem().getId(), orderItem.getMenuItem().getName(), orderItem.getQuantity(), orderItem.getUnitPrice());
    }
}
