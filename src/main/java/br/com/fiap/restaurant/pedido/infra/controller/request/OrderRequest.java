package br.com.fiap.restaurant.pedido.infra.controller.request;

import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Representação de um pedido para criação")
public record OrderRequest(
    @Schema(description = "Lista de itens do pedido", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    @NotEmpty
    List<OrderItemRequest> items
) {
    public CreateOrderInput toCreateOrderInput(Long restaurantId) {
        return new CreateOrderInput(restaurantId, items.stream().map(OrderItemRequest::toOrderRequest).toList());
    }
}
