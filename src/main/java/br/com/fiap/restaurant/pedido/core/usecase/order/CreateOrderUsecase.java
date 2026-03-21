package br.com.fiap.restaurant.pedido.core.usecase.order;

import br.com.fiap.restaurant.pedido.core.domain.MenuItem;
import br.com.fiap.restaurant.pedido.core.domain.Order;
import br.com.fiap.restaurant.pedido.core.domain.OrderItem;
import br.com.fiap.restaurant.pedido.core.domain.StatusOrder;
import br.com.fiap.restaurant.pedido.core.exception.MenuItemNotFoundException;
import br.com.fiap.restaurant.pedido.core.exception.UserNotAuthenticatedException;
import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import br.com.fiap.restaurant.pedido.core.gateway.MenuItemGateway;
import br.com.fiap.restaurant.pedido.core.gateway.OrderGateway;
import br.com.fiap.restaurant.pedido.core.inbound.CreateOrderInput;
import br.com.fiap.restaurant.pedido.core.inbound.OrderItemInput;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateOrderUsecase {

    private final MenuItemGateway menuItemGateway;
    private final OrderGateway orderGateway;
    private final LoggedUserGateway loggedUserGateway;
    
    public CreateOrderUsecase(MenuItemGateway menuItemGateway, OrderGateway orderGateway, LoggedUserGateway loggedUserGateway) {
        this.menuItemGateway = Objects.requireNonNull(menuItemGateway, "menuItemGateway cannot be null.");
        this.orderGateway = Objects.requireNonNull(orderGateway, "orderGateway cannot be null.");
        this.loggedUserGateway = Objects.requireNonNull(loggedUserGateway, "loggedUserGateway cannot be null.");
    }

    public Order create(CreateOrderInput input) {
        Objects.requireNonNull(input, "createOrderInput cannot be null.");

        var customerUuid = loggedUserGateway.getCurrentUser().orElseThrow(UserNotAuthenticatedException::new);
        var orderDateTime = LocalDateTime.now();

        List<OrderItemInput> orderItemsInput = input.orderItemsInput();

        Set<Long> menuItemsId = orderItemsInput.stream().map(OrderItemInput::menuItemId).collect(Collectors.toSet());
        List<MenuItem> items = menuItemGateway.findAllById(menuItemsId);
        Map<Long, MenuItem> itemsMaps = items.parallelStream()
                .collect(Collectors.toMap(MenuItem::getId, item -> item));

        if (itemsMaps.size() != menuItemsId.size()) {
            var notFoundItems = menuItemsId.stream().filter(i -> !itemsMaps.containsKey(i)).toList();
            throw new MenuItemNotFoundException("item(s) not found " + notFoundItems);
        }

        var nonRestaurantItems = items.parallelStream()
                .filter(i -> !i.getRestaurantId().equals(input.restaurantId())).map(MenuItem::getId).toList();
        if (!nonRestaurantItems.isEmpty()) {
            // se existe mas n é do restaurant n deve encontrar
            throw new MenuItemNotFoundException("item(s) not found " + nonRestaurantItems);
        }

        var menuItems = orderItemsInput.parallelStream()
                .map(oii -> new OrderItem(itemsMaps.get(oii.menuItemId()), oii.quantity())).toList();

        var order = new Order(null, input.restaurantId(), customerUuid, menuItems, orderDateTime, StatusOrder.DRAFT);
        return orderGateway.save(order);
    }
}
