package kz.ncanode.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusResponse {
    private Integer status;
    private String message;
}
