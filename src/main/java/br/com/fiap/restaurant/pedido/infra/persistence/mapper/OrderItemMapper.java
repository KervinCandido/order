package br.com.fiap.restaurant.pedido.infra.persistence.mapper;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderItemEntity;

public class OrderItemMapper {

    private OrderItemMapper() {}

    public static OrderItemEntity toOrderItemEntity(OrderItem orderItem, OrderEntity orderEntity) {
        var orderItemEntity = new OrderItemEntity();
        orderItemEntity.setId(orderItem.getId());
        orderItemEntity.setQuantity(orderItem.getQuantity());
        orderItemEntity.setUnitPrice(orderItem.getUnitPrice());
        orderItemEntity.setOrder(orderEntity);
        orderItemEntity.setMenuItem(MenuItemMapper.toMenuItemEntity(orderItem.getMenuItem()));
        return orderItemEntity;
    }

    public static OrderItem toOrderItem(OrderItemEntity orderItemEntity) {
        MenuItem menuItem = MenuItemMapper.toMenuItem(orderItemEntity.getMenuItem());
        return new OrderItem(
            orderItemEntity.getId(),
            menuItem,
            orderItemEntity.getQuantity(),
            orderItemEntity.getUnitPrice()
        );
    }
}
