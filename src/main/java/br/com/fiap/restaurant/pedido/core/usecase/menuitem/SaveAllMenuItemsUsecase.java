package br.com.fiap.restaurant.pedido.core.usecase.menuitem;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.inbound.MenuItemInput;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class SaveAllMenuItemsUsecase {

    private final MenuItemGateway menuItemGateway;

    public SaveAllMenuItemsUsecase(MenuItemGateway menuItemGateway) {
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "menuItemGateway cannot be null");
    }

    public void save(List<MenuItemInput> itemsInput) {
        Objects.requireNonNull(itemsInput, "itemsInput cannot be null");

        List<MenuItem> items = itemsInput
                .parallelStream()
                .map(toMenuItem())
                .toList();
        menuItemGateway.saveAll(items);
    }

    private Function<MenuItemInput, MenuItem> toMenuItem() {
        return i -> new MenuItem(i.id(), i.name(), i.price(), i.restaurantOnly(), i.restaurantId());
    }

}
