# XML.signWithSecurityHeader

Подписывает XML документ в формате `WSSec`, как требуется для работы с сервисом smartbridge (sb.egov.kz).

## Запрос

### Пример запроса

```json
{
	"version": "1.0",
	"method": "XML.signWithSecurityHeader",
	"params": {
		"p12":"MIINkwIBAzCCDU0GCSqGSIb3DQEHAaCCDT4Egg06MIAwgAYJKoZIhvcNAQcBoIAEggWcMIIFmDCCBZQGCyqGSIb3DQEMCgECoIIE+jCCBPYwKAYKKoZIhvcNAQwBAzAaBBQzwa8hA6IVQ8Dkz2V+mhYE/UCE7gICBAAEggTI5tLwNi/DoOWJ7D1S9vKrozFgsHxv/Yjw9jS5C1JNN55xtazMJ3i8pKvvVbY7WqLwXpsJWaS6297OKPkUW5uc9jNLDS8cc37PgaxhA05gKyO97RdGlglnEqxJsy7qsQIEIT66FYOoNnmxpV2Tw3bjEm2xlNLLbbbETnjCyF+M7MFm8g6wA61hisDy1areY3ypTA4Qn5eMoKSvhxlYC+FEAgtLMizwBetfOgkJJk0pBehA+jmmvAzfKikTVW2b7wVtbbISclhkGUoJCh7UojnYNCm5Bbar4MZ+w8lfyLChyFeqazWYzysqjWn1F73EuhFPNvXiCIxPrZ7OJg2lyiYyASCmo46irT60pJerZ0DPAFbAShYfKfg/hvEDuF7/nmdscTp7vaLJic7cQm3i2AkMmB+raPM3UfjYgPwpTKuWL93JRUGQk5UigSZKZIoXDPjJaaxK8RuBtRwEEIkOg4Ip6WBT2R0l23tyP6A+YqlnDvvBSFWrE/8qv2DvNtURt9dVBA+WpmJUBKdisDZxzMaqtva2Z6YGejC1h1BPlCxhzDWmKLyacGbzuhSJvp4lOJmTugNaFUZLLdDgmaTQm4pQ+luhEZiT8X468DVWQYrchwkwmk//xCbp7Tu+7vxljDYZbSDCkHQVCKCUE+B9TN74H4B9zaxpychTXPJ5uIrOQhEUkj1ZpSX9qIwzn8dM2FWqrv1qZEgjcbgmKzYvSoUKZAJJzGBxnRQIIwH6bA0hFXiNQCExhigIz+H02KWqWc3Qmg987y9sr81debtsbceUQR3faGCnRT9cc3ROk9x2kOIcKdX9mDYXh4/sbDtTYiyE5r0A0UtfH0roXANipikayb3U0TQxpjOoMCaAmWRJn1DUtpXD1/3TYBVNQvEl3cysCHFBod/UmmuAS9ZG3OH55R4dXkFFvLum70rGZ9RKjTRJ/4pj414JgBky6jIS2/d3Uww4uCoLa6gtmZN49HwUxAe6/3Fdda27lf++ZbCWAXpqYrJ+vBn3XtqgOLoulqlAW3yITsgidjqx3iXzQPzeucSzJRZbb+2X5Ub6ngsuFcJij7A7/NpfApxjcmRR5YlVTlTr9p+ZZChhRKNamWuf8+SU8kguFlCViZ9H2mhhBcWYjAIWRpPAUAwYUDds5lWfDQofZtS7yQIjUmw4UKrQr0Z7WiN05jx/kbPshWSkrrnWeWNwDwrbFtRA9UHEgwNR4l9m/OdKxDcEX4X/CAX9JriurZonM1BfTX2F2D63rT6bdrOdAa7qNUP+s0Y0VZY8DPqsB0QyYBA6wTnfgZNAnISfnENRlyNTv6/C/6pULFkDztPPRHPThrWRf3ye/n9QrBN2Co6bFkPJ+aRPZhNe4cKAVMBO32Olh6htM8+liWjDeAcKSyoDBWVNYIEt2c5Xg3XHZdpc9L2rNhtypDcXVpjXP/kJ/XZBXojfgQ+SqoWOK0xH+XmVVA8ltNxF4BqgfWLp/dYiaciWTrsj8sazKjhlndiGR7B8oIZ7WbhU0u7+gOsVRQHaFWvZP0CHQvVbObXw4FvtVWgaC6ONztmcFwE4rhYaM+Ju7mcUCY7zCNAL8+AJ0Mxl8fNtkpMXnkt2+5W+ciFHgI2Yv32A55ZHEJJnpwYcwPdlMYGGMCMGCSqGSIb3DQEJFTEWBBRrNhuGTGeWAbZS/jh/YfzZMDwDJzBfBgkqhkiG9w0BCRQxUh5QADYAYgAzADYAMQBiADgANgA0AGMANgA3ADkANgAwADEAYgA2ADUAMgBmAGUAMwA4ADcAZgA2ADEAZgBjAGQAOQAzADAAMwBjADAAMwAyADcAAAAAMIAGCSqGSIb3DQEHBqCAMIACAQAwgAYJKoZIhvcNAQcBMCgGCiqGSIb3DQEMAQYwGgQU5vJcSheCI7r7SLHaZyY7j3lBctcCAgQAoIAEggcokn/1oGk1ZlYLNjFGH1ji2TTrHVUGsVGOcxr9q04HOdQnFWEpA9j3yUGKoBO9MaWUNAiQlAYVUY1JPncr2fdw5bZvWFoQrokox9eUAd2PTMgekUTqjRPv3Mx4/0ej16Wbs0DOTO28iHmUFS8h+dgV5WZQ92EjsQk40rYSCFet6E1GUVAboEVH9T6oVNP8JoHzxmqMiPbmtCqZNlJ3VWbKS1FM1o/8FI+7GKfJJw4+u0lqItpRODHsnboyvDsKqMMOk1Wj9ORH5wTkRRxnELuAemgu5s68SktPv5JDw1O9sUYJrhUnymwG8uyB2fByuudvvGHopU+6iZt7bTYvg8r13JheN/BnYqzzA33U1DIf9LahGqtH7fqNXZzT2BKubqgIZMPDCrx3CisbsUGTV8y7BbYvaD7QZdZrdQnNGrnR7C0bZSr4xUfPjkB/CCi5m6h7jT6FhTH8FU7wY7jpN3/H4fqL0eYw82FAfctcmNk5RLsLbeZ9E8nnhdnF/lIjGmOZJiJGli9BnXfylfvpdTxSQ45vfJsCokLczAZ72Mmvrqg67KvfoKfAjPqIcpTkfqrAZ4d3mNVxx0+bHxUdnLPgg7pp9FfVWZnWClKhV/tjgQ5Dj1tbRH69PnbNdRhpQarZksPE/nDamBQwbFSHAI93D5VTeDElrzuZ7j3T5K/rf8p1aEq4LWSuEMSC16UL7lSwXnan+bHujmviCcx9CdoN1+wDICtKKrWqsyFZoB6LtFdsgSVDQ6zalMGI+0M7c1l8pd92fZON19R9LeSfIQRXpNtYUiEDGlNEFILrZuh1klzLxxbpx7y+S9qPVzhlpoYuDMEbLgv0nPTPz+4LlL8DCx+2v/0cMzjKQbcJ016Vzs0rL5bS1gwb2aZ0ldT4fOSGyUP42YVHxpOw71qcymHmomiNZR9OXaJmarND3bl4ugsbrFPFGsBz1+p7SIwIjCicOj5km2SqYLIdQIubdfLHONtE3J50/NyfUmhySVt3ovdzlEZXpSJw6vGQzRPTr1jxOkLKBRaZ9KymyG/tXMnfRXaiKQ1nh/GZ4S5dlmQCx5YxocYSMavL+HgaBUTSuPM4qs86GRVG9lS/2Vkh9ft/2ufrxUFS9rVPeX8f+saCfDHVlu3P7FLYy/t5pXpnGMh/AjpPAf+wRsxg7xlUPgbfQlvMFzISQCsJawNkyZfoLoxwTilySNNpfMZeigTgHO2gporSosahc5ikTYK+HJLIJNhcnfwmmv+VkdpAZ8nEX8Olxqo6mgVbmNkgAOQUhtva9BkCNx072RgdqHSGm9KFvZD7Jtb6XGZZRFUNM0DNqvMM7rYBfv51o8N6t6corCcOmAJQZ77+nNc6ozF/ZIy8Uy5zRs6Mwa7C9LQ3TiAL7NZHjvA4xnkQ/Srszu6uvKB43VjzPC14pyeWnFVYJbEDf7V4LWU1UZMSRfq+ird7VKgS4AW4CXXsiJfjYkZKHEeYtvnveMAeXlUFWhintyIS8HOU9/e+t2WjTciaXvra1L/YzbKGjri1ECdCUhigFMttCGx/G2PJOcfMNFugz62bJrt6RWMr12WeqpWXGKOqIHaPbTvudnOC0jjD8sBLK2AvFsKujFTfkxOyoTl6BF3zgdSh00qef/xFwhTCNYFSQSQJGFGjw8RhvjAZzow9w37ccN1nUoZopw1aoeimrHKf3s5M6VUhsrpvtphPabIGd5BGTefJKKAUeR0t6BdettRyWT3AsjjM95eDKrcfioCBBPJHOpVdD3YY5o3RawoHLCYCSs8KUaNfTxIv/2/ZQVYQJFTFjLGvjj6WD0qmrCkscEHFRDH++XmJYfuBilF4YLNDC01qJUQdEPATOpSFyVSfJM9oh9LLE0QqC2LfxmxmSBxfNw0ISvACjto1G7xTYYP242DlqL2kqeGuZSYGFpQZIyXzAzYWeotdBjJD3hgGjuuP6wDw5Jyat8U6L1KutYkGDowrxC380rXbRCfbLY5rZvQkHXgXmLHNunyXrM4kqWFwJLPTapptXmkClye8+9O+DfsXkW/rQ8qSWeZnOVPCb3PTIwDAksPpRxjuy4V36z8Vl2GeePSGHWTVxGt383AR50kc5bR1ERw8A4gv6px1g5HWIjnIkP9kIYrvprP4x1rQ0T+B/Tmbf+nMb/oFIs4YQsl+3GlSYph0dAnSx3oaRhFSurU/cTgjPs3nnRTkKc+XX7A5snLtnthD4ZLwO14sPcvHwPAFyEAJXwNTpLbetQpCNUZjUg/id1bGSsp22Q9BaT/GeQyCtEGnRn/UBBLTckoULZi75C2XigUxjrr3o2aGlKJK/CQ01Z25y4G9eYslgLGexuRZs8Lijnx5kznyX7hQEz0wOfQ6DUZH4YMIzsIQa9W06v5GKNRcevZMG+wCfUEtR7yrZzn9TmzregOPGu780WwQt33HZ27Weza5/nI26gaphTYAAAAAAAAAAAAAAAAwPTAhMAkGBSsOAwIaBQAEFOy6U+YkvM72T+xeraexxa7kiKq9BBQctlNgZGGVCnKyqLcEv7BtQC2u/QICBAA=",
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
    "xml": "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Header xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" SOAP-ENV:mustUnderstand=\"1\"><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gost34310-gost34311\"/><ds:Reference URI=\"#id-8bc25bc1-dc4b-4a0b-b48d-2c02e03af620\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gost34311\"/><ds:DigestValue/></ds:Reference></ds:SignedInfo><ds:SignatureValue/></ds:Signature><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n<ds:SignedInfo>\n<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gost34310-gost34311\"/>\n<ds:Reference URI=\"#id-16b6ff3a-84ba-423e-b04b-0e016b53a94b\">\n<ds:Transforms>\n<ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>\n</ds:Transforms>\n<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gost34311\"/>\n<ds:DigestValue>...</ds:DigestValue>\n</ds:Reference>\n</ds:SignedInfo>\n<ds:SignatureValue>...</ds:SignatureValue>\n<ds:KeyInfo>\n<wsse:SecurityTokenReference><wsse:KeyIdentifier EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\" ValueType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3\">...</wsse:KeyIdentifier></wsse:SecurityTokenReference>\n</ds:KeyInfo>\n</ds:Signature></wsse:Security></SOAP-ENV:Header><soap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"id-16b6ff3a-84ba-423e-b04b-0e016b53a94b\"><SendMessage xmlns=\"http://bip.bee.kz/SyncChannel/v10/Types\">...</SendMessage></soap:Body></soap:Envelope>",
    "raw": "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Header xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" SOAP-ENV:mustUnderstand=\"1\"><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\"><ds:SignedInfo><ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/><ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gost34310-gost34311\"/><ds:Reference URI=\"#id-8bc25bc1-dc4b-4a0b-b48d-2c02e03af620\"><ds:Transforms><ds:Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/></ds:Transforms><ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#gost34311\"/><ds:DigestValue/></ds:Reference></ds:SignedInfo><ds:SignatureValue/></ds:Signature></wsse:Security></SOAP-ENV:Header><soap:Body xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" wsu:Id=\"id-8bc25bc1-dc4b-4a0b-b48d-2c02e03af620\"><SendMessage xmlns=\"http://bip.bee.kz/SyncChannel/v10/Types\">...</SendMessage></soap:Body></soap:Envelope>"
  },
  "message": "",
  "status": 0
}
```

### Параметры ответа

- `xml` - Подписанные данные в формате XML
- `raw` - Исходные данные в формате XML
- `tsp` - TSP данные (если указано `createTsp = true`), в формате Base64.