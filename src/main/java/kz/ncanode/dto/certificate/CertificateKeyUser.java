package kz.ncanode.dto.certificate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum CertificateKeyUser {
    INDIVIDUAL("1.2.398.3.3.4.1.1"),
    ORGANIZATION("1.2.398.3.3.4.1.2"),
    CEO("1.2.398.3.3.4.1.2.1"),
    CAN_SIGN("1.2.398.3.3.4.1.2.2"),
    CAN_SIGN_FINANCIAL("1.2.398.3.3.4.1.2.3"),
    HR("1.2.398.3.3.4.1.2.4"),
    EMPLOYEE("1.2.398.3.3.4.1.2.5"),
    NCA_PRIVILEGES("1.2.398.3.3.4.2"),
    NCA_ADMIN("1.2.398.3.3.4.2.1"),
    NCA_MANAGER("1.2.398.3.3.4.2.2"),
    NCA_OPERATOR("1.2.398.3.3.4.2.3"),
    IDENTIFICATION("1.2.398.3.3.4.3"),
    IDENTIFICATION_CON("1.2.398.3.3.4.3.1"),
    IDENTIFICATION_REMOTE("1.2.398.3.3.4.3.2"),
    IDENTIFICATION_REMOTE_DIGITAL_ID("1.2.398.3.3.4.3.2.1")
    ;

    private final String oid;

    public static Optional<CertificateKeyUser> fromOID(String oid) {
        return Arrays.stream(CertificateKeyUser.values())
            .filter(e -> oid.equals(e.getOid()))
            .findFirst();
    }
}
