package br.com.fiap.restaurant.pedido.core.domain;

import br.com.fiap.restaurant.pedido.core.exception.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class MenuItem {

    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final boolean restaurantOnly;
    private final Long restaurantId;

    public MenuItem(Long id, String name, BigDecimal price, boolean restaurantOnly, Long restaurantId) {
        Objects.requireNonNull(name, "name não pode ser nulo.");
        Objects.requireNonNull(price, "price não pode ser nulo.");
        Objects.requireNonNull(restaurantId, "restaurantId não pode ser nulo.");

        if (name.trim().isBlank()) {
            throw new BusinessException("O nome do item não pode ser vazio.");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O preço deve ser maior que zero.");
        }

        this.id = id;
        this.name = name.strip();
        this.price = price;
        this.restaurantOnly = restaurantOnly;
        this.restaurantId = restaurantId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public BigDecimal getTotal() {
        return this.price.multiply(this.price).setScale(2, RoundingMode.HALF_EVEN);
    }

    public boolean isRestaurantOnly() {
        return restaurantOnly;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem that)) return false;

        if (this.id != null || that.id != null) {
            return Objects.equals(this.id, that.id);
        }

        return Objects.equals(this.name, that.name)
                && Objects.equals(this.price, that.price)
                && Objects.equals(this.restaurantOnly, that.restaurantOnly);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : Objects.hash(name, price, restaurantOnly);
    }

}
