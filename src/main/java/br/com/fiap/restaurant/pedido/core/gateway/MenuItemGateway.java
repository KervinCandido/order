package br.com.fiap.restaurant.pedido.core.gateway;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;

import java.util.List;

public interface MenuItemGateway {
    List<MenuItem> findAllById(Iterable<Long> ids);

    void deleteAllByRestaurantId(Long restaurantId);

    void saveAll(List<MenuItem> menuItems);

    void deleteById(Long restaurantId);
}
