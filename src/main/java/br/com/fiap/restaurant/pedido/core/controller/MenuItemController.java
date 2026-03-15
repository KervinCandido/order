package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.UpdateAllMenuItemsInput;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteAllMenuItemsByRestaurantIdUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.UpdateAllMenuItemsUsecase;

import java.util.Objects;

public class MenuItemController {

    private final UpdateAllMenuItemsUsecase updateAllMenuItemsUsecase;
    private final DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase;

    public MenuItemController(UpdateAllMenuItemsUsecase updateAllMenuItemsUsecase, DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase) {
        this.updateAllMenuItemsUsecase = Objects.requireNonNull(updateAllMenuItemsUsecase, "updateAllMenuItemsUsecase cannot be null.");
        this.deleteAllMenuItemsByRestaurantIdUsecase = Objects.requireNonNull(deleteAllMenuItemsByRestaurantIdUsecase, "deleteAllMenuItemsByRestaurantIdUsecase cannot be null");
    }

    public void updateAllMenuItems(UpdateAllMenuItemsInput input) {
        Objects.requireNonNull(input, "updateAllMenuItemsInput cannot be null");
        updateAllMenuItemsUsecase.update(input);
    }

    public void deleteByRestaurantId(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null");
        deleteAllMenuItemsByRestaurantIdUsecase.deleteByRestaurantId(restaurantId);
    }
}
