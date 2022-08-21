package kz.ncanode.dto.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CertificateSubject {
    private final String commonName;
    private final String lastName;
    private final String surName;
    private final String gender;
    private final LocalDateTime birthDate;
    private final String iin;
    private final String country;
    private final String locality;
    private final String state;
    private final String dn;
}
