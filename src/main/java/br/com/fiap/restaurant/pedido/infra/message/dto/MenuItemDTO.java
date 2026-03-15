package br.com.fiap.restaurant.pedido.infra.message.dto;

import java.math.BigDecimal;

public record MenuItemDTO(Long id, String name, BigDecimal price, Boolean restaurantOnly) {}
