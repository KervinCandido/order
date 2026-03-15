package br.com.fiap.restaurant.pedido.infra.persistence.adapter;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.infra.persistence.mapper.MenuItemMapper;
import br.com.fiap.restaurant.pedido.infra.persistence.repository.MenuItemRepository;

import java.util.List;

public class MenuItemGatewayAdapter implements MenuItemGateway {

    private final MenuItemRepository menuItemRepository;

    public MenuItemGatewayAdapter(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItem> findAllById(Iterable<Long> ids) {
        return menuItemRepository.findAllById(ids)
                .parallelStream()
                .map(MenuItemMapper::toMenuItem).toList();
    }

    @Override
    public void deleteAllByRestaurantId(Long restaurantId) {
        menuItemRepository.deleteAllByRestaurantId(restaurantId);
    }

    @Override
    public void saveAll(List<MenuItem> menuItems) {
        var menuItemsEntities = menuItems.parallelStream().map(MenuItemMapper::toMenuItemEntity).toList();
        menuItemRepository.saveAll(menuItemsEntities);
    }
}
