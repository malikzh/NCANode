package kz.ncanode.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@Data
@SuperBuilder
public abstract class StatusResponse {

    @Builder.Default
    private Integer status = HttpStatus.OK.value();

    @Builder.Default
    private String message = "OK";

}
