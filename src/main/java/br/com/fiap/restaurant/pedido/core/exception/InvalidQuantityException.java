package br.com.fiap.restaurant.pedido.core.exception;

public class InvalidQuantityException extends BusinessException {
    public InvalidQuantityException(String message) {
        super(message);
    }
}
