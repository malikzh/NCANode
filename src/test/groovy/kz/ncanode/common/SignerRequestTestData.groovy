package kz.ncanode.common

import kz.ncanode.dto.request.SignerRequest

class SignerRequestTestData implements WithTestData {

    /**
     * Запросы на подпись
     */
    public final static Closure<SignerRequest> SIGNER_REQUEST_VALID_2004 = () -> SignerRequest.builder().key(KEY_INDIVIDUAL_VALID_SIGN_2004).password(KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD).keyAlias(null).build()
    public final static Closure<SignerRequest> SIGNER_REQUEST_VALID_2015 = () -> SignerRequest.builder().key(KEY_INDIVIDUAL_VALID_2015).password(KEY_INDIVIDUAL_VALID_2015_PASSWORD).keyAlias(null).build()
    public final static Closure<SignerRequest> SIGNER_REQUEST_VALID_2004_WITH_REFERENCE = () -> SignerRequest.builder().referenceUri('#' + REFERENCE_URI).key(KEY_INDIVIDUAL_VALID_SIGN_2004).password(KEY_INDIVIDUAL_VALID_SIGN_2004_PASSWORD).keyAlias(null).build()
    public final static Closure<SignerRequest> SIGNER_REQUEST_VALID_2015_WITH_REFERENCE = () -> SignerRequest.builder().referenceUri('#' + REFERENCE_URI).key(KEY_INDIVIDUAL_VALID_2015).password(KEY_INDIVIDUAL_VALID_2015_PASSWORD).keyAlias(null).build()

}
