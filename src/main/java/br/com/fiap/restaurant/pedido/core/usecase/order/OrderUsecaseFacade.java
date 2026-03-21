package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class OrderUsecaseFacade {

    private final PendingOrderUseCase pendingOrderUseCase;
    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final CreateOrderUsecase createOrderUsecase;
    private final PayOrderUseCase payOrderUseCase;
    private final FindOrderByCurrentUserUsecase findOrderByCurrentUserUsecase;
    private final FindOrderByIdUsecase findOrderByIdUsecase;

    private OrderUsecaseFacade(Builder builder) {
        this.pendingOrderUseCase = Objects.requireNonNull(builder.pendingOrderUseCase, "pendingOrderUseCase cannot be null");
        this.confirmOrderUseCase = Objects.requireNonNull(builder.confirmOrderUseCase, "confirmOrderUseCase cannot be null");
        this.createOrderUsecase = Objects.requireNonNull(builder.createOrderUsecase, "createOrderUsecase cannot be null");
        this.payOrderUseCase = Objects.requireNonNull(builder.payOrderUseCase, "payOrderUseCase cannot be null");
        this.findOrderByCurrentUserUsecase = Objects.requireNonNull(builder.findOrderByCurrentUserUsecase, "findOrderByCurrentUserUsecase cannot be null");
        this.findOrderByIdUsecase  = Objects.requireNonNull(builder.findOrderByIdUsecase, "findOrderByIdUsecase cannot be null");
    }

    public void pendingOrder(Long orderId) {
        pendingOrderUseCase.pendingOrderById(orderId);
    }

    public void confirmOrder(Long orderId) {
        confirmOrderUseCase.confirmOrderBy(orderId);
    }

    public Order createOrder(CreateOrderInput input) {
        return createOrderUsecase.create(input);
    }

    public void payOrder(Long orderId) {
        payOrderUseCase.payOrderById(orderId);
    }

    public Optional<Order> findOrderById(Long orderId) {
        return findOrderByIdUsecase.findById(orderId);
    }

    public Page<Order> findOrderByCurrentUser(Set<StatusOrder> orderStatus, int pageNumber, int pageSize) {
        return findOrderByCurrentUserUsecase.findOrderByCurrentUser(orderStatus, pageNumber, pageSize);
    }

    public static class Builder {
        private PendingOrderUseCase pendingOrderUseCase;
        private ConfirmOrderUseCase confirmOrderUseCase;
        private CreateOrderUsecase createOrderUsecase;
        private PayOrderUseCase payOrderUseCase;
        private FindOrderByCurrentUserUsecase findOrderByCurrentUserUsecase;
        private FindOrderByIdUsecase findOrderByIdUsecase;

        public Builder pendingOrderUseCase(PendingOrderUseCase pendingOrderUseCase) {
            this.pendingOrderUseCase = pendingOrderUseCase;
            return this;
        }

        public Builder confirmOrderUseCase(ConfirmOrderUseCase confirmOrderUseCase) {
            this.confirmOrderUseCase = confirmOrderUseCase;
            return this;
        }

        public Builder createOrderUsecase(CreateOrderUsecase createOrderUsecase) {
            this.createOrderUsecase = createOrderUsecase;
            return this;
        }

        public Builder payOrderUseCase(PayOrderUseCase payOrderUseCase) {
            this.payOrderUseCase = payOrderUseCase;
            return this;
        }

        public Builder findOrderByCurrentUserUsecase(FindOrderByCurrentUserUsecase findOrderByCurrentUserUsecase) {
            this.findOrderByCurrentUserUsecase = findOrderByCurrentUserUsecase;
            return this;
        }

        public Builder findOrderByIdUsecase(FindOrderByIdUsecase findOrderByIdUsecase) {
            this.findOrderByIdUsecase = findOrderByIdUsecase;
            return this;
        }

        public OrderUsecaseFacade build() {
            return new OrderUsecaseFacade(this);
        }
    }
}
