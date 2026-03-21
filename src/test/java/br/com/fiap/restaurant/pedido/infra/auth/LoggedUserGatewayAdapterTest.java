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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
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
            var userUuid = UUID.randomUUID();
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn(userUuid.toString());

            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            assertThat(result).isPresent().contains(userUuid);
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se a autenticação for nula")
        void deveRetornarOptionalVazioSeAutenticacaoForNula() {
            given(securityContext.getAuthentication()).willReturn(null);

            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se o usuário não estiver autenticado")
        void deveRetornarOptionalVazioSeUsuarioNaoEstiverAutenticado() {
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(false);

            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar Optional vazio se o nome do usuário na autenticação for nulo")
        void deveRetornarOptionalVazioSeNomeDoUsuarioForNulo() {
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn(null);

            Optional<UUID> result = loggedUserGatewayAdapter.getCurrentUser();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção se o nome do usuário for uma string inválida para UUID")
        void deveLancarExcecaoSeNomeNaoForUuidValido() {
            var invalidUuidString = "not-a-uuid";
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getName()).willReturn(invalidUuidString);

            assertThatThrownBy(() -> loggedUserGatewayAdapter.getCurrentUser())
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Cenários de verificação de permissão (role)")
    class HasRoleScenarios {

        private final String roleName = "ROLE_USER";

        @Test
        @DisplayName("Deve retornar verdadeiro se o usuário autenticado possuir a permissão")
        void deveRetornarVerdadeiroSeUsuarioPossuirPermissao() {
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getAuthorities()).willAnswer(a ->  List.of(new SimpleGrantedAuthority(roleName)));

            boolean result = loggedUserGatewayAdapter.hasRole(roleName);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("Deve retornar falso se o usuário autenticado não possuir a permissão")
        void deveRetornarFalsoSeUsuarioNaoPossuirPermissao() {
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getAuthorities()).willAnswer(a -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

            boolean result = loggedUserGatewayAdapter.hasRole(roleName);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Deve retornar falso se a autenticação for nula")
        void deveRetornarFalsoSeAutenticacaoForNula() {
            given(securityContext.getAuthentication()).willReturn(null);

            boolean result = loggedUserGatewayAdapter.hasRole(roleName);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Deve retornar falso se o usuário não estiver autenticado")
        void deveRetornarFalsoSeUsuarioNaoEstiverAutenticado() {
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(false);

            boolean result = loggedUserGatewayAdapter.hasRole(roleName);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Deve retornar falso se a lista de permissões for vazia")
        void deveRetornarFalsoSeListaDePermissoesForVazia() {
            given(securityContext.getAuthentication()).willReturn(authentication);
            given(authentication.isAuthenticated()).willReturn(true);
            given(authentication.getAuthorities()).willReturn(Collections.emptyList());

            boolean result = loggedUserGatewayAdapter.hasRole(roleName);

            assertThat(result).isFalse();
        }
    }
}
