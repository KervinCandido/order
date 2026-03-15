package br.com.fiap.restaurant.pedido.infra.controller.request;

import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
    @NotNull
    @NotEmpty
    List<OrderItemRequest> items
) {
    public CreateOrderInput toCreateOrderInput(Long restaurantId) {
        return new CreateOrderInput(restaurantId, items.stream().map(OrderItemRequest::toOrderRequest).toList());
    }
}
