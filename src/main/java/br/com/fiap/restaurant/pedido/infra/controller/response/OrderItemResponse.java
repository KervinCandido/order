package br.com.fiap.restaurant.pedido.infra.controller.response;

import br.com.fiap.restaurant.pedido.core.outbound.OrderItemOutput;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Representa um item de um pedido")
public record OrderItemResponse (
        @Schema(description = "ID do item do cardápio", example = "1")
        Long menuItemId,
        @Schema(description = "Nome do item", example = "Hambúrguer")
        String name,
        @Schema(description = "Quantidade do item", example = "2")
        BigDecimal quantity,
        @Schema(description = "Preço unitário do item", example = "25.50")
        BigDecimal price,
        @Schema(description = "Preço total para o item (quantidade * preço unitário)", example = "51.00")
        BigDecimal total
) {
    public OrderItemResponse(OrderItemOutput orderItemOutput) {
        this (
            orderItemOutput.menuItemId(),
            orderItemOutput.name(),
            orderItemOutput.quantity(),
            orderItemOutput.unitPrice(),
            orderItemOutput.total()
        );
    }
}
