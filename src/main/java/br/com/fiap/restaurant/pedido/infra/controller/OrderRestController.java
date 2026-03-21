package br.com.fiap.restaurant.pedido.infra.controller;

import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.infra.controller.request.OrderRequest;
import br.com.fiap.restaurant.pedido.infra.controller.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/restaurants/{restaurant-id}/orders")
@Tag(name = "Pedidos", description = "Operações para gerenciamento de pedidos.")
public class OrderRestController {

    private final OrderController orderController;

    public OrderRestController(OrderController orderController) {
        this.orderController = orderController;
    }

    @Operation(summary = "Criar um novo pedido", description = "Cria um novo pedido para um restaurante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos para a criação do pedido")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> create(@PathVariable("restaurant-id") Long restaurantId,
                                      @Valid @RequestBody OrderRequest orderRequest,
                                      UriComponentsBuilder uriComponentsBuilder) {
        var newOrder = orderController.create(orderRequest.toCreateOrderInput(restaurantId));
        var uri = uriComponentsBuilder.path("/restaurants/{restaurant-id}/orders/{order-menuItemId}").buildAndExpand(restaurantId, newOrder.id()).toUri();
        return ResponseEntity.created(uri).body(new OrderResponse(newOrder));
    }

    @Operation(summary = "Confirmar um pedido", description = "Confirma o recebimento de um pedido para preparo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pedido confirmado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PostMapping("/confirm/{order-id}")
    public ResponseEntity<Void> confirm(@PathVariable("order-id") Long orderId) {
        orderController.confirm(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar um pedido pelo order id", description = "Busca um pedido pelo identificador do pedido informado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{order-id}")
    public ResponseEntity<OrderResponse> findById(@PathVariable("order-id") Long orderId) {
        return orderController.findById(orderId)
                .map(OrderResponse::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // TODO  Consultar todos os pedidos associados ao cliente autenticado.
    // TODO Alterar enquanto em draft
}
