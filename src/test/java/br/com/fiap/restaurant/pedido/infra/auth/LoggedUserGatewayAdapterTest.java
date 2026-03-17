package br.com.fiap.restaurant.pedido.infra.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o adapter LoggedUserGatewayAdapter")
class LoggedUserGatewayAdapterTest {

    private LoggedUserGatewayAdapter loggedUserGatewayAdapter;
    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        loggedUserGatewayAdapter = new LoggedUserGatewayAdapter();
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        given(SecurityContextHolder.getContext()).willReturn(securityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Nested
    @DisplayName("Cenários de obtenção do usuário atual")
    class GetCurrentUserScenarios {

        @Test
        @DisplayName("Deve retornar o UUID do usuário quando ele estiver autenticado")
        void deveRetornarUuidDoUsuarioAutenticado() {
            // Given
            var userUuid = UUID.randomUUID();
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn(userUuid.toString());

            // When
            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            // Then
            assertThat(result).isPresent().contains(userUuid);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se a autenticação for nula")
        void deveRetornarOptionalVazioSeAutenticacaoForNula() {
            // Given
            given(securityContext.getAuthentication()).willReturn(null);

            // When
            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se o usuário não estiver autenticado")
        void deveRetornarOptionalVazioSeUsuarioNaoEstiverAutenticado() {
            // Given
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(false);

            // When
            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se o nome do usuário na autenticação for nulo")
        void deveRetornarOptionalVazioSeNomeDoUsuarioForNulo() {
            // Given
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn(null);

            // When
            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se o nome do usuário for uma string inválida para UUID")
        void deveLancarExcecaoSeNomeNaoForUuidValido() {
            // Given
            var invalidUuidString = "not-a-uuid";
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn(invalidUuidString);

            // When & Then
            assertThatThrownBy(() -> loggedUserGatewayAdapter.getCurrentUser())
                .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
