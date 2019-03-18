# XML.sign

## Запрос

### Пример запроса

```json
{
	"version": "1.0",
	"method": "XML.sign",
	"params": {
		"p12":"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",
		"password":"Qwerty12",
		"xml": "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><name>NCANode</name></root>"
	}
}
```

### Параметры запроса

- `p12` - Закодированный файл c ключом p12, в формате [Base64](https://ru.wikipedia.org/wiki/Base64)
- `password` - Пароль для ключа
- `xml` - Данные в формате XML, которые надо подписать
- `createTsp` - Если указано `true`, то будет создана метка TSP
- `useTsaPolicy` - Указание политики TSP. Может быть одним из двух параметров
    - `TSA_GOST_POLICY` - ГОСТ новый НУЦ *(по-умолчанию)*
    - `TSA_GOSTGT_POLICY` - ГОСТ с OID текущего НУЦ
- `tspHashAlgorithm` - Алгоритм хэширования для создания подписи TSP. Может быть одним из следующих значений:
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

## Ответ

### Пример ответа

```json
{
    "result": {
        "xml": "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><root><name>NCANode</name><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\r\n<ds:SignedInfo>\r\n<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>\r\n<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\r\n<ds:Reference URI=\"\">\r\n<ds:Transforms>\r\n<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\r\n<ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/>\r\n</ds:Transforms>\r\n<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\r\n<ds:DigestValue>ybvg7uzrmIoa6Q02yU8BiLjYNl64fr+yXCtg0kHwdv4=</ds:DigestValue>\r\n</ds:Reference>\r\n</ds:SignedInfo>\r\n<ds:SignatureValue>\r\niAo6o5tfMBmXAxOxNTPVZGgjBebA9+taJyywl+h8e992FJYOBr84w92RSk9gwG+VAZXrHaSVsIo5\r\ngirvJVwNfDwIyX0to2z5d2/XRa780uNMHTR3oF5RQ97miZqDemBu7PVwG1ayEOMIrmdbnl8+Qr8G\r\n19I/CaSmViifwt/XGBalIZxIdFNjT6v0NIPizDLlJcHL8tNe73Sjj4f5Ux11oYQ/Ml8l7UhVtYOw\r\nsz/rWIwXqAYocXilUno0CwVKTR26JfIJELyBmrn+9aKXDdusrGfc+T+2ADKVd/52G656Pe2fDghd\r\nKfSRBfpSO1UrZLWBsiMAybQEkgvz7VgGjfmixA==\r\n</ds:SignatureValue>\r\n<ds:KeyInfo>\r\n<ds:X509Data>\r\n<ds:X509Certificate>\r\nMIIGZTCCBE2gAwIBAgIUFX1cSXpU/SdXs4r74PS8YFuVbAowDQYJKoZIhvcNAQELBQAwUjELMAkG\r\nA1UEBhMCS1oxQzBBBgNVBAMMOtKw0JvQotCi0KvSmiDQmtCj05jQm9CQ0J3QlNCr0KDQo9Co0Ksg\r\n0J7QoNCi0JDQm9Cr0pogKFJTQSkwHhcNMTgwODIyMTIxMTM2WhcNMTkwODIyMTIxMTM2WjCBpzEe\r\nMBwGA1UEAwwV0KLQldCh0KLQntCSINCi0JXQodCiMRUwEwYDVQQEDAzQotCV0KHQotCe0JIxGDAW\r\nBgNVBAUTD0lJTjEyMzQ1Njc4OTAxMTELMAkGA1UEBhMCS1oxFTATBgNVBAcMDNCQ0JvQnNCQ0KLQ\r\nqzEVMBMGA1UECAwM0JDQm9Cc0JDQotCrMRkwFwYDVQQqDBDQotCV0KHQotCe0JLQmNCnMIIBIjAN\r\nBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtKWLOJf9qCqA6EO/SVtiMuPZ8q3Sg2RjO0dWXqKQ\r\nRP7BWhIyMucMv+WmpRs8RuJ987Hm3B/JszSdiPrmtA9BpIERKphRwp3n4QR6pfLUBEp+5QNetNsv\r\n+dbiPcefWCzgJZCqEZVbPvSkiFH20y13YQ2FhEBUp4lLOqydBD2CsDVoTusvLanEgR+AdziJPq2+\r\niXwhttpNPShKRTXGbGkxUa4P7YMUCUqWstR7svLaJqxKDMhaR7MpEt56a2pfntm5oFxKNFoBQjRX\r\nKbiBNIKciMRAeznjezv9ZA98WzWPIMuWzi38fPW5X7IVqa7ZbAFWvZIHWJmrl57uKGBNd9EUewID\r\nAQABo4IB2zCCAdcwDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggqgw4DAwQB\r\nATAPBgNVHSMECDAGgARbanQRMB0GA1UdDgQWBBRrNhuGTGeWAbZS/jh/YfzZMDwDJzBeBgNVHSAE\r\nVzBVMFMGByqDDgMDAgQwSDAhBggrBgEFBQcCARYVaHR0cDovL3BraS5nb3Yua3ovY3BzMCMGCCsG\r\nAQUFBwICMBcMFWh0dHA6Ly9wa2kuZ292Lmt6L2NwczBWBgNVHR8ETzBNMEugSaBHhiFodHRwOi8v\r\nY3JsLnBraS5nb3Yua3ovbmNhX3JzYS5jcmyGImh0dHA6Ly9jcmwxLnBraS5nb3Yua3ovbmNhX3Jz\r\nYS5jcmwwWgYDVR0uBFMwUTBPoE2gS4YjaHR0cDovL2NybC5wa2kuZ292Lmt6L25jYV9kX3JzYS5j\r\ncmyGJGh0dHA6Ly9jcmwxLnBraS5nb3Yua3ovbmNhX2RfcnNhLmNybDBiBggrBgEFBQcBAQRWMFQw\r\nLgYIKwYBBQUHMAKGImh0dHA6Ly9wa2kuZ292Lmt6L2NlcnQvbmNhX3JzYS5jZXIwIgYIKwYBBQUH\r\nMAGGFmh0dHA6Ly9vY3NwLnBraS5nb3Yua3owDQYJKoZIhvcNAQELBQADggIBACy0Lxj0D/q3SwUz\r\n0X9BICyKPw/U6sXmedqUcrghzZuT9ojnUp9w7g4ndZOKTRRxQyLiUYb9neJ3SGVuF/XYcs7Ovrp5\r\nRGNNHuVUR8bQz9cbWd/O2qRUY6qlg4ZSjYsjFYaQm8o+uO56PuqWG125O7XNUdAUHNBc2hUrrngG\r\nKU0FKxlBygxLpvTf4I9q3QA0PJ6MnHrUKlor4sRGar4hMJCbrxeMG4pv3Jx/r9fsKy7f+yZeQo3T\r\n4XAIXmUTXF8UC3HtIroxAP6yEoEhG76oS3qvYc1K/krI48ju5VYxmzEabNqRhiiEBpocIwCqFLLo\r\n9x3CKuUkuA7pwEib4YcCNxCTucCtd9x8dGgZRNffJV4de/Aja/VP84q8rxmcyogbUQzvPb+2/zKR\r\nh6cxYxnRsuL4wWUV+fxp/usy0mJMboQF7IcRFe1fXosU0RWYmKHITOCDbs0NKxTn7TSxEKMYdJN6\r\nYngCmKlmwR/+AfxhN1QMSQpU/m8Glwl+f5wZIL5MQJVhrrWIteh0tnb+OuDQHz4g2vmD2xq5jUQD\r\nFIrjXOdy4zToqM6tirt3nGDsblWgcgsPac50FLT1+um7W26UsmtZ9/wXvkxYC9kL5gUX53VD/bcK\r\nki8fogjrNoYEZiORRqmwvZ5EVe4w3Hfb7YCnc3NzhhIg6hqmzumXNCgLCt2q\r\n</ds:X509Certificate>\r\n</ds:X509Data>\r\n</ds:KeyInfo>\r\n</ds:Signature></root>"
    },
    "message": "",
    "status": 0
}
```

### Параметры ответа

- `xml` - Подписанные данные в формате XML
- `tsp` - TSP данные (если указано `createTsp = true`), в формате Base64.