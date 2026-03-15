package br.com.fiap.restaurant.pedido.infra.controller;

import br.com.fiap.restaurant.pedido.core.controller.OrderController;
import br.com.fiap.restaurant.pedido.infra.controller.request.OrderRequest;
import br.com.fiap.restaurant.pedido.infra.controller.response.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/{restaurant-menuItemId}/orders")
public class OrderRestController {

    private final OrderController orderController;

    public OrderRestController(OrderController orderController) {
        this.orderController = orderController;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@PathVariable("restaurant-menuItemId") Long restaurantId,
                                      @Valid @RequestBody OrderRequest orderRequest,
                                      UriComponentsBuilder uriComponentsBuilder) {
        var newOrder = orderController.create(orderRequest.toCreateOrderInput(restaurantId));
        var uri = uriComponentsBuilder.path("/{restaurant-menuItemId}/orders/{order-menuItemId}").buildAndExpand(restaurantId, newOrder.id()).toUri();
        return ResponseEntity.created(uri).body(new OrderResponse(newOrder));
    }

    @PostMapping("/confirm/{order-id}")
    public ResponseEntity<Void> confirm(@PathVariable("order-id") Long orderId) {
        orderController.confirm(orderId);
        return ResponseEntity.noContent().build();
    }
}
