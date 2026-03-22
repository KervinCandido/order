package br.com.fiap.restaurant.pedido.infra.controller;

import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.domain.pagination.Page;
import br.com.fiap.restaurant.pedido.infra.controller.request.OrderRequest;
import br.com.fiap.restaurant.pedido.infra.controller.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

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

    @Operation(
        summary = "Buscar pedidos do usuário autenticado", description = "Busca todos os pedidos associados ao cliente autenticado.",
        parameters = {
            @Parameter(name = "pageNumber", description = "Número da página", example = "0"),
            @Parameter(name = "pageSize", description = "Tamanho da página", example = "10"),
            @Parameter(name = "statusOrders", description = "Lista de status para filtrar os pedidos", example = "DRAFT,PAYED")
        }
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagina com pedidos encontrados"),
    })
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> findOrdersByCurrentUser(
            @RequestParam(required = false, name = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false, name = "statusOrders") Set<StatusOrder> statusOrders) {
        var page = orderController.findOrderByCurrentUser(statusOrders, pageNumber, pageSize);
        return ResponseEntity.ok(page.mapItems(OrderResponse::new));
    }
}
