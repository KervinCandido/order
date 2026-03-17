package br.com.fiap.restaurant.pedido.infra.service;

import br.com.fiap.restaurant.pedido.core.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o serviço JwtService")
class JwtServiceTest {

    @Mock
    private JwtDecoder jwtDecoder;
    @Mock
    private Jwt jwt;

    private JwtService jwtService;

    private static final String FAKE_TOKEN = "fake-token";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(jwtDecoder);
    }

    @Nested
    @DisplayName("Validação de Token")
    class IsValidToken {

        @Test
        @DisplayName("Deve retornar true para um token válido e não expirado")
        void deveRetornarTrueParaTokenValido() {
            // Given
            given(jwtDecoder.decode(FAKE_TOKEN)).willReturn(jwt);
            given(jwt.getExpiresAt()).willReturn(Instant.now().plusSeconds(3600));

            // When
            boolean isValid = jwtService.isValidToken(FAKE_TOKEN);

            // Then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Deve lançar exceção para um token expirado")
        void deveLancarExcecaoParaTokenExpirado() {
            // Given
            given(jwtDecoder.decode(FAKE_TOKEN)).willReturn(jwt);
            given(jwt.getExpiresAt()).willReturn(Instant.now().minusSeconds(1));

            // When & Then
            assertThatThrownBy(() -> jwtService.isValidToken(FAKE_TOKEN))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("The token has expired.");
        }

        @Test
        @DisplayName("Deve retornar false se o decoder lançar JwtException")
        void deveRetornarFalseSeDecoderLancarExcecao() {
            // Given
            given(jwtDecoder.decode(FAKE_TOKEN)).willThrow(new JwtException("Invalid token"));

            // When
            boolean isValid = jwtService.isValidToken(FAKE_TOKEN);

            // Then
            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Deve lançar exceção se o token não tiver data de expiração")
        void deveLancarExcecaoSeTokenNaoTiverDataExpiracao() {
            // Given
            given(jwtDecoder.decode(FAKE_TOKEN)).willReturn(jwt);
            given(jwt.getExpiresAt()).willReturn(null);

            // When & Then
            assertThatThrownBy(() -> jwtService.isValidToken(FAKE_TOKEN))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("The token is invalid.");
        }

        @Test
        @DisplayName("Deve lançar exceção se o token decodificado for nulo")
        void deveLancarExcecaoSeTokenDecodificadoForNulo() {
            // Given
            given(jwtDecoder.decode(FAKE_TOKEN)).willReturn(null);

            // When & Then
            assertThatThrownBy(() -> jwtService.isValidToken(FAKE_TOKEN))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("The token is invalid.");
        }
    }

    @Nested
    @DisplayName("Extração de Dados do Token")
    class GetDataFromToken {

        @Test
        @DisplayName("Deve retornar o ID do usuário (subject) do token")
        void deveRetornarIdDoUsuario() {
            // Given
            var userId = UUID.randomUUID().toString();
            given(jwtDecoder.decode(FAKE_TOKEN)).willReturn(jwt);
            given(jwt.getSubject()).willReturn(userId);

            // When
            String result = jwtService.getUserId(FAKE_TOKEN);

            // Then
            assertThat(result).isEqualTo(userId);
        }

        @Test
        @DisplayName("Deve retornar as authorities (roles) do token")
        void deveRetornarAuthorities() {
            // Given
            given(jwtDecoder.decode(FAKE_TOKEN)).willReturn(jwt);
            given(jwt.getClaimAsString("roles")).willReturn("ROLE_USER ROLE_ADMIN");

            // When
            Collection<GrantedAuthority> authorities = jwtService.getAuthorities(FAKE_TOKEN);

            // Then
            assertThat(authorities).containsExactlyInAnyOrder(
                    new SimpleGrantedAuthority("ROLE_USER"),
                    new SimpleGrantedAuthority("ROLE_ADMIN")
            );
        }
    }

    @Nested
    @DisplayName("Validação do Construtor")
    class ConstructorValidation {

        @Test
        @DisplayName("Deve lançar NullPointerException se JwtDecoder for nulo")
        void deveLancarExcecaoSeJwtDecoderForNulo() {
            // When & Then
            assertThatThrownBy(() -> new JwtService(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("jwtDecoder cannot be null");
        }
    }
}
