package br.com.fiap.restaurant.pedido.core.inbound;

import java.util.List;

public record UpdateAllMenuItemsInput(Long restaurantId, List<MenuItemInput> menuItems) {}
