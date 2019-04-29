# RAW.sign

Подписывает любые данные. Подпись предоставляется в формате CMS

# Запрос

### Параметры запроса

- `raw` - Данные которые необходимо подписать (закодированные в формате Base64)
- `createTsp` - (Boolean) Если указано, то будет создана ещё подпись TSP
- `p12` - Ключ, которым будут подписываться данные (закодированный в формате Base64)
- `password` - Пароль ключа
- `useTsaPolicy` - Указание политики TSP. Может быть одним из двух параметров
    - `TSA_GOST_POLICY` - ГОСТ новый НУЦ *(по-умолчанию)*
    - `TSA_GOSTGT_POLICY` - ГОСТ с OID текущего НУЦ
- `tspHashAlgorithm` - Алгоритм хэширования для создания подписи TSP. *Данный параметр не учитывается, если `tspInCms == true`.* Может быть одним из следующих значений:
    - `MD5`
    - `SHA1`
    - `SHA224`
    - `SHA256`
    - `SHA384`
    - `SHA512`
    - `RIPEMD128`
    - `RIPEMD160`
    - `RIPEMD256`
    - `GOST34311GT`
    - `GOST34311` *(по-умолчанию)*
- `tspInCms` - (Boolean) Если true, то TSP метка будет вставляться непосредственно в CMS

### Пример запроса

```json
{
	"version": "1.0",
	"method":"RAW.sign",
	"params": {
		"raw": "YXNkYXNk",
		"createTsp": false,
		"p12":"MIINkwIBAzCCDU0...",
		"password":"Qwerty12"
	}
}
```

## Ответ

### Пример ответа

```json
{
    "result": {
        "tsp": "MIIF/AYJKoZIhvcNAQcCoIIF7TCCB...",
        "cms": "MIIIrwYJKoZIhvcNAQcCoIIIoDCCC..."
    },
    "message": "",
    "status": 0
}
```


### Параметры ответа

- `cms` - CMS-подпись закодированная в формате Base64
- `tsp` - Если указано `createTsp = true`, это TSP подпись в формате Base64.
