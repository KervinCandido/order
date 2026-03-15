package br.com.fiap.restaurant.pedido.core.usecase.menuitem;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;
import br.com.fiap.restaurant.pedido.core.inbound.UpdateAllMenuItemsInput;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class UpdateAllMenuItemsUsecase {

    private final MenuItemGateway menuItemGateway;

    public UpdateAllMenuItemsUsecase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = menuItemGateway;
    }

    public void update(UpdateAllMenuItemsInput input) {
        Objects.requireNonNull(input, "updateAllMenuItemsInput cannot be null");

        menuItemGateway.deleteAllByRestaurantId(input.restaurantId());
        List<MenuItem> items = input.menuItems()
                .parallelStream()
                .map(toMenuItems(input.restaurantId()))
                .toList();
        menuItemGateway.saveAll(items);
    }

    private Function<MenuItemInput, MenuItem> toMenuItems(Long restaurantId) {
        return i -> new MenuItem(i.id(), i.name(), i.price(), i.restaurantOnly(), restaurantId);
    }
}
