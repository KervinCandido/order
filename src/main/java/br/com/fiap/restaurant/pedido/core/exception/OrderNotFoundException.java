package br.com.fiap.restaurant.pedido.core.exception;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
