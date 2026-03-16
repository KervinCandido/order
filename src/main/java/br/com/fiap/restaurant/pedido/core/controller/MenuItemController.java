package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteAllMenuItemsByRestaurantIdUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.DeleteMenuItemUsecase;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.SaveAllMenuItemsUsecase;

import java.util.List;
import java.util.Objects;

public class MenuItemController {

    private final SaveAllMenuItemsUsecase saveAllMenuItemsUsecase;
    private final DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase;
    private final DeleteMenuItemUsecase deleteMenuItemUsecase;

    public MenuItemController(SaveAllMenuItemsUsecase saveAllMenuItemsUsecase,
                              DeleteAllMenuItemsByRestaurantIdUsecase deleteAllMenuItemsByRestaurantIdUsecase,
                              DeleteMenuItemUsecase deleteMenuItemUsecase) {
        this.saveAllMenuItemsUsecase = Objects.requireNonNull(saveAllMenuItemsUsecase, "updateAllMenuItemsUsecase cannot be null.");
        this.deleteAllMenuItemsByRestaurantIdUsecase = Objects.requireNonNull(deleteAllMenuItemsByRestaurantIdUsecase, "deleteAllMenuItemsByRestaurantIdUsecase cannot be null");
        this.deleteMenuItemUsecase = Objects.requireNonNull(deleteMenuItemUsecase, "deleteMenuItemUsecase cannot be null");
    }

    public void createMenuItems(List<MenuItemInput> input) {
        Objects.requireNonNull(input, "updateAllMenuItemsInput cannot be null");
        saveAllMenuItemsUsecase.save(input);
    }

    public void deleteByRestaurantId(Long restaurantId) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null");
        deleteAllMenuItemsByRestaurantIdUsecase.deleteByRestaurantId(restaurantId);
    }

    public void updateMenuItem(MenuItemInput menuInput) {
        Objects.requireNonNull(menuInput, "menuItemInput cannot be null");
        saveAllMenuItemsUsecase.save(List.of(menuInput));
    }

    public void updateAllMenuItemsOfRestaurant(Long restaurantId, List<MenuItemInput> menuItemsInput) {
        Objects.requireNonNull(restaurantId, "restaurantId cannot be null");
        Objects.requireNonNull(menuItemsInput, "menuItemsInput cannot be null");
        deleteAllMenuItemsByRestaurantIdUsecase.deleteByRestaurantId(restaurantId);
        saveAllMenuItemsUsecase.save(menuItemsInput);
    }

    public void deleteMenuItem(Long menuItemId) {
        Objects.requireNonNull(menuItemId, "menuItemInput cannot be null");
        deleteMenuItemUsecase.deleteById(menuItemId);
    }
}
