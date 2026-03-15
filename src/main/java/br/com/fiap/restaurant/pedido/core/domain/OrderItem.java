package br.com.fiap.restaurant.pedido.core.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class OrderItem {

    private final Long id;
    private final MenuItem menuItem;
    private final BigDecimal quantity;
    private final BigDecimal unitPrice;

    public OrderItem(Long id, MenuItem menuItem, BigDecimal quantity, BigDecimal unitPrice) {
        this.menuItem = Objects.requireNonNull(menuItem, "menuItem cannot be null.");
        this.quantity = Objects.requireNonNull(quantity, "quantity cannot be null.");
        this.unitPrice = Objects.requireNonNull(unitPrice, "quantity cannot be null.");
        this.id = id;

        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The quantity must be greater than zero.");
        }

        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("The unit price must be greater than zero.");
        }
    }

    public OrderItem(MenuItem menuItem, BigDecimal quantity) {
        this(null, menuItem, quantity, menuItem.getPrice());
    }

    public Long getId() {
        return id;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotal() {
        return this.unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_EVEN);
    }
}
