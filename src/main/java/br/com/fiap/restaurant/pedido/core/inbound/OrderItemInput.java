package br.com.fiap.restaurant.pedido.core.inbound;

import java.math.BigDecimal;
import java.util.Objects;

public record OrderItemInput(Long menuItemId, BigDecimal quantity) {
    public OrderItemInput {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null.");
        Objects.requireNonNull(quantity, "quantity cannot be null.");

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The quantity must be greater than zero.");
        }
    }
}
