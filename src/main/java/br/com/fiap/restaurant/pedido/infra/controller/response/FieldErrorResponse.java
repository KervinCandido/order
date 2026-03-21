package br.com.fiap.restaurant.pedido.infra.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa um erro de validação de campo em uma requisição")
public record FieldErrorResponse(
    @Schema(description = "Nome do campo que causou o erro", example = "cpf")
    String field,
    @Schema(description = "Mensagem de erro detalhada", example = "CPF inválido")
    String message
) {}
