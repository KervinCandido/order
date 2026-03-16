package br.com.fiap.restaurant.pedido.core.usecase.menuitem;

import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;

import java.util.Objects;

public class DeleteMenuItemUsecase {

    private final MenuItemGateway menuItemGateway;

    public DeleteMenuItemUsecase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "menuItemGateway cannot be null");
    }

    public void deleteById(Long menuItemId) {
        Objects.requireNonNull(menuItemId, "menuItemId cannot be null");
        menuItemGateway.deleteById(menuItemId);
    }
}
