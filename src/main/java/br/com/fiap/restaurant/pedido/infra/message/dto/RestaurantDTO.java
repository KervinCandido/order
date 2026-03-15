package br.com.fiap.restaurant.pedido.infra.message.dto;

import java.util.Set;

public record RestaurantDTO(Long id, Set<MenuItemDTO> menu) {}
