package br.com.fiap.restaurant.pedido.core.domain;

import br.com.fiap.restaurant.pedido.core.exception.BusinessException;

import java.math.BigDecimal;
import java.util.Objects;

public class MenuItem {

    private final Long id;
    private final String name;
    private final BigDecimal unitPrice;
    private final boolean restaurantOnly;
    private final Long restaurantId;

    public MenuItem(Long menuItemId, String name, BigDecimal unitPrice, boolean restaurantOnly, Long restaurantId) {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(unitPrice, "unitPrice cannot be null");
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null");

        if (name.isBlank()) {
            throw new BusinessException("name cannot be empty");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("unitPrice must be greater than zero");
        }

        this.id = menuItemId;
        this.name = name.strip();
        this.unitPrice = unitPrice;
        this.restaurantOnly = restaurantOnly;
        this.restaurantId = restaurantId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public boolean isRestaurantOnly() {
        return restaurantOnly;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem that)) return false;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
