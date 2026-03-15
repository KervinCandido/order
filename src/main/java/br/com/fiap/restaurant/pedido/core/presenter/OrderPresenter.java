package br.com.fiap.restaurant.pedido.core.presenter;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;

public class OrderPresenter {

    private OrderPresenter() {}

    public static OrderOutput toOutput(Order order) {
        return new OrderOutput(
            order.getId(),
            order.getRestaurantId(),
            order.getCustomerUuid(),
            order.getStatus(),
            order.getItems().stream().map(OrderItemPresenter::toOutput).toList(),
            order.getOrderDateTime(),
            order.getTotal()
        );
    }
}
