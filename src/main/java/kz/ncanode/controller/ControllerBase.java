package kz.ncanode.controller;

import kz.ncanode.dto.response.StatusResponse;
import kz.ncanode.exception.ClientException;
import kz.ncanode.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerBase {

    @ExceptionHandler(ClientException.class)
    protected ResponseEntity<StatusResponse> handleClientException(ClientException e, WebRequest request) {
        var response = e.toStatusResponse();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(ServerException.class)
    protected ResponseEntity<StatusResponse> handleServerException(ServerException e, WebRequest request) {
        var response = e.toStatusResponse();
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<StatusResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(StatusResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(e.getMessage())
            .build()
        );
    }

}
