package br.com.fiap.restaurant.pedido.core.outbound;

import java.math.BigDecimal;

public record OrderItemOutput (
        Long menuItemId,
        String name,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal total
) {}
