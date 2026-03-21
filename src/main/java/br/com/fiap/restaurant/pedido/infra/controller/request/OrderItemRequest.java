package br.com.fiap.restaurant.pedido.infra.controller.request;

import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Representação de um item de um pedido")
public record OrderItemRequest(
    @Schema(description = "ID do item do menu", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Positive
    Long menuItemId,
    @Schema(description = "Quantidade do item", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @Positive
    BigDecimal quantity
) {
    public OrderItemInput toOrderRequest() {
        return new OrderItemInput(menuItemId, quantity);
    }
}
