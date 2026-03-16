package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.exception.BusinessException;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;

import java.util.Objects;

public class PendingOrderUseCase {

    private final OrderGateway orderGateway;

    public PendingOrderUseCase(OrderGateway orderGateway) {
        this.orderGateway = Objects.requireNonNull(orderGateway, "OrderGateway cannot be null");
    }

    public void pendingOrderById(Long orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null");
        Order order = orderGateway.findById(orderId).orElseThrow(() -> new BusinessException("Order not found"));
        order.pendingPay();
        orderGateway.save(order);
    }
}
