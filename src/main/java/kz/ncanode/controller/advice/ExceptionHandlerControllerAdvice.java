package kz.ncanode.controller.advice;

import kz.ncanode.configuration.SystemConfiguration;
import kz.ncanode.dto.response.ErrorResponse;
import kz.ncanode.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerControllerAdvice {
    private final SystemConfiguration systemConfiguration;


    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e, WebRequest request) {
        Integer status = HttpStatus.INTERNAL_SERVER_ERROR.value();

        if (e instanceof ApplicationException) {
            status = ((ApplicationException)e).getStatus();
        }

        String details = null;

        if (systemConfiguration.isDetailedErrors() && Objects.nonNull(e.getCause())) {
            details = e.getCause().getMessage();
        }

        var response = ErrorResponse.builder()
            .status(status)
            .message(e.getMessage())
            .details(details)
            .build();

        return ResponseEntity.status(status).body(response);
    }

}
