package br.com.fiap.restaurant.pedido.core.exception;

public class InvalidOrderStateException extends BusinessException {

    public InvalidOrderStateException(String message) {
        super(message);
    }

    public InvalidOrderStateException() {
        super("The requested operation cannot be performed because of the current order status.");
    }
}
