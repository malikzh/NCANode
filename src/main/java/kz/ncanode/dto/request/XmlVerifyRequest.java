package kz.ncanode.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class XmlVerifyRequest extends VerifyRequest {
    private String xml;
}
