package br.com.fiap.restaurant.pedido.core.outbound;

import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderOutput(
        Long id,
        Long restaurantId,
        UUID customerUuid,
        StatusOrder status,
        List<OrderItemOutput> items,
        LocalDateTime orderDateTime,
        BigDecimal total
) {}
