package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.UpdateAllMenuItemsInput;
import br.com.fiap.restaurant.pedido.core.usecase.menuitem.UpdateAllMenuItemsUsecase;

import java.util.Objects;

public class MenuItemController {

    private final UpdateAllMenuItemsUsecase updateAllMenuItemsUsecase;

    public MenuItemController(UpdateAllMenuItemsUsecase updateAllMenuItemsUsecase) {
        this.updateAllMenuItemsUsecase = Objects.requireNonNull(updateAllMenuItemsUsecase, "updateAllMenuItemsUsecase cannot be null.");
    }

    public void updateAllMenuItems(UpdateAllMenuItemsInput input) {
        Objects.requireNonNull(input, "uodateAllMenuItemsInput cannot be null");
        updateAllMenuItemsUsecase.update(input);
    }
}
