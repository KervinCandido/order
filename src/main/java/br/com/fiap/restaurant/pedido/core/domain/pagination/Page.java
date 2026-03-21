package br.com.fiap.restaurant.pedido.core.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record Page<T>(int pageNumber, int pageSize, long totalElements, List<T> content, int totalPages) {

    public Page(int pageNumber, int pageSize, long totalElements, List<T> content) {
        this(pageNumber, pageSize, totalElements, content, pageSize <= 0 ? 0 : (int) Math.ceil((double) totalElements / (double) pageSize));
    }

    public <R> Page<R> mapItems(Function<T, R> mapper) {
        return new Page<>(
                pageNumber,
                pageSize,
                totalElements,
                content.stream().map(mapper).toList()
        );
    }
}
