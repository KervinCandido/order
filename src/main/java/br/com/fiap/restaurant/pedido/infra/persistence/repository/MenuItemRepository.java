package br.com.fiap.restaurant.pedido.infra.persistence.repository;

import br.com.fiap.restaurant.pedido.infra.persistence.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItemEntity, Long> {
    void deleteAllByRestaurantId(Long restaurantId);
}
