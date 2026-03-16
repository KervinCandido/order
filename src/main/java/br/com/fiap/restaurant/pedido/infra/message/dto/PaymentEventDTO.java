package br.com.fiap.restaurant.pedido.infra.message.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentEventDTO(
        UUID paymentId,
        Long orderId,
        UUID clientId,
        BigDecimal amount,
        String status) {}
