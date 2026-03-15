package br.com.fiap.restaurant.pedido.core.exception;

public class MenuItemNotFoundException extends BusinessException {
    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
