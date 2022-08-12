package kz.ncanode.exception;

import kz.ncanode.dto.response.StatusResponse;

public abstract class ExceptionBase extends RuntimeException {
    protected ExceptionBase(String message) {
        super(message);
    }

    abstract Integer getStatus();

    public StatusResponse toStatusResponse() {
        return StatusResponse.builder()
                .message(getMessage())
                .status(getStatus())
                .build();
    }
}
