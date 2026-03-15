package br.com.fiap.restaurant.pedido.core.inbound;

import java.util.List;
import java.util.Objects;

public record CreateOrderInput(Long restaurantId, List<OrderItemInput> orderItemsInput) {
    public CreateOrderInput {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null.");
        Objects.requireNonNull(orderItemsInput, "orderItemsInput cannot be null.");
    }
}
