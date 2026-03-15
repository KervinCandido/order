package br.com.fiap.restaurant.pedido.infra.controller.request;

import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequest(
    @NotNull
    @Positive
    Long menuItemId,
    @NotNull
    @Positive
    BigDecimal quantity
) {
    public OrderItemInput toOrderRequest() {
        return new OrderItemInput(menuItemId, quantity);
    }
}
