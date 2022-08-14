package kz.ncanode.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse extends StatusResponse {
    private String details;
}
