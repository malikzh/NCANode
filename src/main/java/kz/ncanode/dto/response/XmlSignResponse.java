package kz.ncanode.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class XmlSignResponse {
    private String xml;
}
