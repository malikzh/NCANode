package kz.ncanode.constants;

public class MessageConstants {

    /* KeyService */
    public final static String KEY_INVALID_BASE64            = "Key reading error: Invalid Base64 format. Key must be in valid Base64 format.";
    public final static String KEY_INVALID_FORMAT            = "Key reading error: Invalid format.";
    public final static String KEY_INVALID_PASSWORD          = "Key reading error: Password incorrect.";
    public final static String KEY_UNKNOWN_ERROR             = "Key reading error: Unknown error. Please see logs.";
    public final static String KEY_ENGINE_ERROR              = "Key reading error: Engine error. Please see logs.";
    public final static String KEY_ALIASES_NOT_FOUND         = "Key reading error: Key does not have aliases.";
    public final static String KEY_ALIAS_NOT_FOUND           = "Key reading error: Key does not have '%s' alias";
    public final static String KEY_CANT_EXTRACT_PRIVATE_KEY  = "Key reading error: Cannot extract private key.";
    public final static String KEY_CANT_EXTRACT_CERTIFICATE  = "Key reading error: Cannot extract certificate.";
    public final static String CERT_INVALID                  = "[%d]: Invalid certificate given.";
}
