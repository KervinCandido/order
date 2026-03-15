package br.com.fiap.restaurant.pedido.infra.auth;

import br.com.fiap.restaurant.pedido.core.gateway.LoggedUserGateway;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class LoggedUserGatewayAdapter implements LoggedUserGateway {

    @Override
    public Optional<UUID> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) return Optional.empty();

        return Optional.of(UUID.fromString(auth.getName()));
    }

}
