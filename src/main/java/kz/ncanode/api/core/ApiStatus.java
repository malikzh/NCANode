package kz.ncanode.api.core;

/**
 * Статусы API
 */
public class ApiStatus {

    public final static int STATUS_OK                    = 0;                    // Всё ОК
    public final static int STATUS_VERSION_NOT_SPECIFIED = 1; // Не указана версия API
    public final static int STATUS_VERSION_NOT_SUPPORTED = 2; // Версия не поддерживается
    public final static int STATUS_INVALID_PARAMETER     = 3; // Не указан параметр
    public final static int STATUS_METHOD_NOT_FOUND      = 4; // Метод не найден
    public final static int STATUS_API_ERROR             = 5; // Внутренняя ошибка API
    public final static int STATUS_PARAMS_NOT_FOUND      = 6; // Параметр "params" отсутствует
    public final static int STATUS_METHOD_NOT_SPECIFIED  = 7; // Метод не указан
    public final static int STATUS_REQUEST_PARSE_ERROR   = -1; // Ошибка парсинга запроса
    public final static int STATUS_CERTIFICATE_INVALID   = 8; // На подпись передан невалидный сертификат

}
