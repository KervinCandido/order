package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.exception.BusinessException;
import br.com.fiap.restaurant.pedido.core.exception.OperationNotAllowedException;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.core.gateway.PublisherGateway;

import java.util.Objects;
import java.util.UUID;

public class ConfirmOrderUseCase {

    private final LoggedUserGateway loggedUserGateway;
    private final OrderGateway orderGateway;
    private final PublisherGateway<Order> confirmOrderPublisher;

    public ConfirmOrderUseCase(LoggedUserGateway loggedUserGateway, OrderGateway orderGateway, PublisherGateway<Order> confirmOrderPublisher) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "LoggedUserGateway cannot be null");
        this.orderGateway = Objects.requireNonNull(orderGateway, "OrderGateway cannot be null");
        this.confirmOrderPublisher = Objects.requireNonNull(confirmOrderPublisher, "ConfirmOrderPublisher cannot be null");
    }

    public void confirm(Long orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null");

        Order order = orderGateway.findById(orderId).orElseThrow(() -> new BusinessException("Order not found"));

        UUID customerUuid = order.getCustomerUuid();
        UUID currentUserUUid = loggedUserGateway.getCurrentUser().orElseThrow(UserNotAuthenticatedException::new);

        if (!customerUuid.equals(currentUserUUid)) {
            throw new OperationNotAllowedException("Current user cannot confirm this order");
        }

        order.confirm();
        orderGateway.save(order);
        confirmOrderPublisher.publish(order);
    }
}
