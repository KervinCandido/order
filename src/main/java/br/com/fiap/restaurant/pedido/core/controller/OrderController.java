package br.com.fiap.restaurant.pedido.core.controller;

import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import br.com.fiap.restaurant.pedido.core.presenter.OrderPresenter;
import br.com.fiap.restaurant.pedido.core.usecase.order.ConfirmOrderUseCase;
import br.com.fiap.restaurant.pedido.core.usecase.order.CreateOrderUsecase;

import java.util.Objects;

public class OrderController {

    private final CreateOrderUsecase createOrderUsecase;
    private final ConfirmOrderUseCase confirmOrderUseCase;

    public OrderController(CreateOrderUsecase createOrderUsecase, ConfirmOrderUseCase confirmOrderUseCase) {
        this.createOrderUsecase = Objects.requireNonNull(createOrderUsecase, "createOrderUsecase cannot be null.");
        this.confirmOrderUseCase = Objects.requireNonNull(confirmOrderUseCase, "confirmOrderUseCase cannot be null.");
    }

    public OrderOutput create(CreateOrderInput input) {
        Objects.requireNonNull(input, "createOrderInput cannot be null.");
        var order = createOrderUsecase.create(input);
        return OrderPresenter.toOutput(order);
    }

    public void confirm(Long orderId) {
        Objects.requireNonNull(orderId, "orderId cannot be null.");
        confirmOrderUseCase.confirm(orderId);
    }
}
