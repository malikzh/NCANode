package kz.ncanode.common

import kz.ncanode.dto.request.SignerRequest
import spock.lang.Shared

trait WithSignerRequests {

    @Shared
    final SignerRequest SIGNER_REQUEST_1 = SignerRequest.builder()
        .key("key1")
        .password("password1")
        .keyAlias("keyAlias1")
        .build()

    @Shared
    final SignerRequest SIGNER_REQUEST_2 = SignerRequest.builder()
        .key("key2")
        .password("password2")
        .keyAlias("keyAlias2")
        .build()
}
