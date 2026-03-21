package br.com.fiap.restaurant.pedido.core.domain.pagination;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Teste para a classe Page")
class PageTest {

    @Nested
    @DisplayName("Dado uma página criada")
    class DadoUmaPaginaCriada {

        private final int pageNumber = 1;
        private final int pageSize = 10;
        private final long totalElements = 25;
        private final List<String> content = List.of("item1", "item2");
        private final Page<String> page = new Page<>(pageNumber, pageSize, totalElements, content);

        @Test
        @DisplayName("Então deve retornar o número da página corretamente")
        void entaoDeveRetornarNumeroPaginaCorretamente() {
            assertThat(page.pageNumber()).isEqualTo(pageNumber);
        }

        @Test
        @DisplayName("Então deve retornar o tamanho da página corretamente")
        void entaoDeveRetornarTamanhoPaginaCorretamente() {
            assertThat(page.pageSize()).isEqualTo(pageSize);
        }

        @Test
        @DisplayName("Então deve retornar o total de elementos corretamente")
        void entaoDeveRetornarTotalElementosCorretamente() {
            assertThat(page.totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("Então deve retornar o conteúdo corretamente")
        void entaoDeveRetornarConteudoCorretamente() {
            assertThat(page.content()).isEqualTo(content);
        }

        @Test
        @DisplayName("Então deve calcular o total de páginas corretamente")
        void entaoDeveCalcularTotalPaginasCorretamente() {
            int totalPages = (int) Math.ceil((double) totalElements / (double) pageSize);
            assertThat(page.totalPages()).isEqualTo(totalPages);
        }

        @Test
        @DisplayName("Então deve retornar 0 para o total de páginas quando o tamanho da página é 0")
        void entaoDeveRetornarZeroTotalPaginasQuandoPageSizeZero() {
            Page<String> pageWithZeroPageSize = new Page<>(pageNumber, 0, totalElements, content);
            assertThat(pageWithZeroPageSize.totalPages()).isZero();
        }

        @Test
        @DisplayName("Então deve mapear os itens da página para outro tipo")
        void entaoDeveMapearItensParaOutroTipo() {
            Page<Integer> mappedPage = page.mapItems(String::length);
            assertThat(mappedPage.content()).containsExactly(5, 5);
        }

        @Test
        @DisplayName("Então deve retornar uma página com conteúdo vazio ao mapear com lista vazia")
        void entaoDeveRetornarPaginaComConteudoVazioAoMapearComListaVazia() {
            Page<String> emptyPage = new Page<>(1, 10, 0, Collections.emptyList());
            Page<Integer> mappedPage = emptyPage.mapItems(String::length);
            assertThat(mappedPage.content()).isEmpty();
        }
    }
}
