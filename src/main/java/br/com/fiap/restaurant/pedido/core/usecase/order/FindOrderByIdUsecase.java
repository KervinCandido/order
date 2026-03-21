package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;

import java.util.Objects;
import java.util.Optional;

public class FindOrderByIdUsecase {

    private final LoggedUserGateway loggedUserGateway;
    private final OrderGateway orderGateway;

    public FindOrderByIdUsecase(LoggedUserGateway loggedUserGateway, OrderGateway orderGateway) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null");
        this.orderGateway = Objects.requireNonNull(orderGateway, "orderGateway cannot be null");
    }

    public Optional<Order> findById(Long orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null");

        var userUuid = loggedUserGateway.getCurrentUser().orElseThrow(UserNotAuthenticatedException::new);

        var orderOptional = orderGateway.findById(orderId);
        if (orderOptional.isEmpty()) {
            return Optional.empty();
        }

        var order = orderOptional.get();

        if (order.getCustomerUuid().equals(userUuid) || loggedUserGateway.hasRole(Order.VIEW_ORDER)) {
            return Optional.of(order);
        }

        return Optional.empty();
    }
}
