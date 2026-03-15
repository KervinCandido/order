package br.com.fiap.restaurant.pedido.core.gateway;

import java.util.Optional;
import java.util.UUID;

public interface LoggedUserGateway {
    Optional<UUID> getCurrentUser();
}
