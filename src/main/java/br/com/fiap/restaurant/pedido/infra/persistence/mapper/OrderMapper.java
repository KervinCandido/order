package br.com.fiap.restaurant.pedido.infra.persistence.mapper;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.infra.persistence.entity.OrderEntity;

public class OrderMapper {
    private OrderMapper() {}

    public static OrderEntity toOrderEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(order.getId());
        orderEntity.setRestaurantId(order.getRestaurantId());
        orderEntity.setCustomerUuid(order.getCustomerUuid());
        orderEntity.setOrderDateTime(order.getOrderDateTime());
        orderEntity.setStatusOrder(order.getStatus());
        orderEntity.setOrderItems(order.getItems()
                .stream()
                .map(orderItem -> OrderItemMapper.toOrderItemEntity(orderItem, orderEntity))
                .toList());
        return orderEntity;
    }

    public static Order toOrder(OrderEntity orderEntity) {
        return new Order(
            orderEntity.getId(),
            orderEntity.getRestaurantId(),
            orderEntity.getCustomerUuid(),
            orderEntity.getOrderItems()
                    .stream()
                    .map(OrderItemMapper::toOrderItem)
                    .toList(),
            orderEntity.getOrderDateTime(),
            orderEntity.getStatusOrder()
        );
    }
}
