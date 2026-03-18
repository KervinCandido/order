package br.com.fiap.restaurant.pedido.infra.persistence.mapper;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;

public class MenuItemMapper {

    private MenuItemMapper(){}

    public static MenuItemEntity toMenuItemEntity(MenuItem menuItem) {
        var entity = new MenuItemEntity();
        entity.setId(menuItem.getId());
        entity.setName(menuItem.getName());
        entity.setUnitPrice(menuItem.getUnitPrice());
        entity.setRestaurantId(menuItem.getRestaurantId());
        return entity;
    }

    public static MenuItem toMenuItem(MenuItemEntity menuItemEntity) {
        return new MenuItem(menuItemEntity.getId(), menuItemEntity.getName(), menuItemEntity.getUnitPrice(), menuItemEntity.isRestaurantOnly(), menuItemEntity.getRestaurantId());
    }
}
