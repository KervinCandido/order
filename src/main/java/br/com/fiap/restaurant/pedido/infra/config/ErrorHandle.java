package br.com.fiap.restaurant.pedido.infra.config;

import br.com.fiap.restaurant.pedido.core.exception.*;
import br.com.fiap.restaurant.pedido.infra.controller.response.FieldErrorResponse;
import br.com.fiap.restaurant.pedido.infra.controller.response.SimpleErrorResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ErrorHandle {

    private final MessageSource messageSource;

    public ErrorHandle(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ApiResponse(responseCode = "403", description = "Operação não permitida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code =  HttpStatus.FORBIDDEN)
    @ExceptionHandler({OperationNotAllowedException.class})
    public SimpleErrorResponse handleOperationNotAllowedException(OperationNotAllowedException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "403", description = "Acesso negado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public SimpleErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UsernameNotFoundException.class)
    public SimpleErrorResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialsException.class)
    public SimpleErrorResponse handleInvalidCredentialsException(InvalidCredentialsException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "401", description = "Usuário não autenticado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UserNotAuthenticatedException.class)
    public SimpleErrorResponse handleUserNotAuthenticatedException(UserNotAuthenticatedException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar este pedido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(OrderOwnershipException.class)
    public SimpleErrorResponse handleOrderOwnershipException(OrderOwnershipException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public SimpleErrorResponse handleBusinessException(BusinessException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "422", description = "Mudança de estado do pedido inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SimpleErrorResponse.class)))
    @ResponseStatus(code = HttpStatus.UNPROCESSABLE_CONTENT)
    @ExceptionHandler(InvalidOrderStateException.class)
    public SimpleErrorResponse handleInvalidOrderStateException(InvalidOrderStateException e) {
        return new SimpleErrorResponse(e.getMessage());
    }

    @ApiResponse(responseCode = "400", description = "Erro de validação", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FieldErrorResponse.class))))
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public List<FieldErrorResponse> handle(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        List<FieldErrorResponse> errors = new ArrayList<>();

        fieldErrors.forEach(error -> {
            String field  = error.getField();
            String message = messageSource.getMessage(error, LocaleContextHolder.getLocale());
            errors.add(new FieldErrorResponse(field, message));
        });

        return errors;
    }
}
