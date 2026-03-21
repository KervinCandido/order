package br.com.fiap.restaurant.pedido.infra.controller.response;

import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.outbound.OrderOutput;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Representa um pedido realizado")
public record OrderResponse(
        @Schema(description = "ID do pedido", example = "1")
        Long id,
        @Schema(description = "UUID do cliente", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479")
        UUID customerUuid,
        @Schema(description = "Data e hora do pedido", example = "2024-07-21T10:00:00")
        LocalDateTime orderDateTime,
        @Schema(description = "Status do pedido", example = "PAYED")
        StatusOrder status,
        @Schema(description = "Lista de itens do pedido")
        List<OrderItemResponse> items,
        @Schema(description = "Valor total do pedido", example = "150.75")
        BigDecimal total
) {
    public OrderResponse(OrderOutput order) {
        this(order.id(), order.customerUuid(), order.orderDateTime(), order.status(),
                order.items().stream().map(OrderItemResponse::new).toList(), order.total());
    }
}
