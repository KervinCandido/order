package br.com.fiap.restaurant.pedido.core.exception;

public class OrderOwnershipException extends BusinessException {

    public OrderOwnershipException() {
        super("Current user cannot confirm this order");
    }
}
