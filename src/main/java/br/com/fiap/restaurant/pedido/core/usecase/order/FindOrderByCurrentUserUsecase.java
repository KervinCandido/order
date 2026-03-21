package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class FindOrderByCurrentUserUsecase {

    public static final int DEFAULT_PAGE_NUMBER = 0;
    public static final int DEFAULT_PAGE_SIZE = 10;

    private final LoggedUserGateway loggedUserGateway;
    private final OrderGateway orderGateway;

    public FindOrderByCurrentUserUsecase(LoggedUserGateway loggedUserGateway, OrderGateway orderGateway) {
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null");
        this.orderGateway = Objects.requireNonNull(orderGateway, "orderGateway cannot be null");
    }

    public Page<Order> findOrderByCurrentUser(Set<StatusOrder> orderStatus, int pageNumber, int pageSize) {
        if (pageNumber < 0) pageNumber = DEFAULT_PAGE_NUMBER;
        if (pageSize < 1) pageSize = DEFAULT_PAGE_SIZE;

        UUID userUuid = loggedUserGateway.getCurrentUser().orElseThrow(UserNotAuthenticatedException::new);
        return orderStatus == null || orderStatus.isEmpty()
                ? orderGateway.findByUser(userUuid, pageNumber, pageSize)
                : orderGateway.findByUserAndStatus(userUuid, orderStatus, pageNumber, pageSize);
    }
}
