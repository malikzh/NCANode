package kz.ncanode.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@Data
public class TspCreateRequest {
    @NotEmpty
    private String xml;
}