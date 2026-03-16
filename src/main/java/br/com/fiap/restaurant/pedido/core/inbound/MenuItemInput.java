package br.com.fiap.restaurant.pedido.core.inbound;

import java.math.BigDecimal;

public record MenuItemInput(Long id, String name, BigDecimal price, boolean restaurantOnly, Long restaurantId) {}
