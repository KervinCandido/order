package br.com.fiap.restaurant.pedido.core.usecase.menuitem;

import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;

import java.util.Objects;

public class DeleteAllMenuItemsByRestaurantIdUsecase {

    private final MenuItemGateway menuItemGateway;

    public DeleteAllMenuItemsByRestaurantIdUsecase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "menuItemGateway cannot be null");
    }

    public void deleteByRestaurantId(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null");
        menuItemGateway.deleteAllByRestaurantId(restaurantId);
    }
}
