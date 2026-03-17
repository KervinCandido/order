package br.com.fiap.restaurant.pedido.core.presenter;

import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.core.outbound.OrderItemOutput;

public class OrderItemPresenter {

    private OrderItemPresenter() {}

    public static OrderItemOutput toOutput(OrderItem orderItem) {
        return new OrderItemOutput(
            orderItem.getMenuItem().getId(),
            orderItem.getMenuItem().getName(),
            orderItem.getQuantity(),
            orderItem.getMenuItem().getUnitPrice(),
            orderItem.getTotal()
        );
    }
}
