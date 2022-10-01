package kz.ncanode.dto.response;

import kz.ncanode.dto.cms.CmsSignerInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Jacksonized
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CmsVerificationResponse extends StatusResponse  {
    public boolean valid;
    public List<CmsSignerInfo> signers;
}
