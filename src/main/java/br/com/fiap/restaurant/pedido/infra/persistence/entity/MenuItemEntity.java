package br.com.fiap.restaurant.pedido.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "menu_item")
public class MenuItemEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "restaurant_only", nullable = false)
    private boolean restaurantOnly;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isRestaurantOnly() {
        return restaurantOnly;
    }

    public void setRestaurantOnly(boolean restaurantOnly) {
        this.restaurantOnly = restaurantOnly;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
