# XML.verify

## Запрос

### Пример запроса

```json
{
	"version": "1.0",
	"method": "XML.verify",
	"params": {
		"xml":"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?><root><name>NCANode</name><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\r\n<ds:SignedInfo>\r\n<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>\r\n<ds:SignatureMethod Algorithm=\"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256\"/>\r\n<ds:Reference URI=\"\">\r\n<ds:Transforms>\r\n<ds:Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"/>\r\n<ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/>\r\n</ds:Transforms>\r\n<ds:DigestMethod Algorithm=\"http://www.w3.org/2001/04/xmlenc#sha256\"/>\r\n<ds:DigestValue>ybvg7uzrmIoa6Q02yU8BiLjYNl64fr+yXCtg0kHwdv4=</ds:DigestValue>\r\n</ds:Reference>\r\n</ds:SignedInfo>\r\n<ds:SignatureValue>\r\niAo6o5tfMBmXAxOxNTPVZGgjBebA9+taJyywl+h8e992FJYOBr84w92RSk9gwG+VAZXrHaSVsIo5\r\ngirvJVwNfDwIyX0to2z5d2/XRa780uNMHTR3oF5RQ97miZqDemBu7PVwG1ayEOMIrmdbnl8+Qr8G\r\n19I/CaSmViifwt/XGBalIZxIdFNjT6v0NIPizDLlJcHL8tNe73Sjj4f5Ux11oYQ/Ml8l7UhVtYOw\r\nsz/rWIwXqAYocXilUno0CwVKTR26JfIJELyBmrn+9aKXDdusrGfc+T+2ADKVd/52G656Pe2fDghd\r\nKfSRBfpSO1UrZLWBsiMAybQEkgvz7VgGjfmixA==\r\n</ds:SignatureValue>\r\n<ds:KeyInfo>\r\n<ds:X509Data>\r\n<ds:X509Certificate>\r\nMIIGZTCCBE2gAwIBAgIUFX1cSXpU/SdXs4r74PS8YFuVbAowDQYJKoZIhvcNAQELBQAwUjELMAkG\r\nA1UEBhMCS1oxQzBBBgNVBAMMOtKw0JvQotCi0KvSmiDQmtCj05jQm9CQ0J3QlNCr0KDQo9Co0Ksg\r\n0J7QoNCi0JDQm9Cr0pogKFJTQSkwHhcNMTgwODIyMTIxMTM2WhcNMTkwODIyMTIxMTM2WjCBpzEe\r\nMBwGA1UEAwwV0KLQldCh0KLQntCSINCi0JXQodCiMRUwEwYDVQQEDAzQotCV0KHQotCe0JIxGDAW\r\nBgNVBAUTD0lJTjEyMzQ1Njc4OTAxMTELMAkGA1UEBhMCS1oxFTATBgNVBAcMDNCQ0JvQnNCQ0KLQ\r\nqzEVMBMGA1UECAwM0JDQm9Cc0JDQotCrMRkwFwYDVQQqDBDQotCV0KHQotCe0JLQmNCnMIIBIjAN\r\nBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtKWLOJf9qCqA6EO/SVtiMuPZ8q3Sg2RjO0dWXqKQ\r\nRP7BWhIyMucMv+WmpRs8RuJ987Hm3B/JszSdiPrmtA9BpIERKphRwp3n4QR6pfLUBEp+5QNetNsv\r\n+dbiPcefWCzgJZCqEZVbPvSkiFH20y13YQ2FhEBUp4lLOqydBD2CsDVoTusvLanEgR+AdziJPq2+\r\niXwhttpNPShKRTXGbGkxUa4P7YMUCUqWstR7svLaJqxKDMhaR7MpEt56a2pfntm5oFxKNFoBQjRX\r\nKbiBNIKciMRAeznjezv9ZA98WzWPIMuWzi38fPW5X7IVqa7ZbAFWvZIHWJmrl57uKGBNd9EUewID\r\nAQABo4IB2zCCAdcwDgYDVR0PAQH/BAQDAgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMCBggqgw4DAwQB\r\nATAPBgNVHSMECDAGgARbanQRMB0GA1UdDgQWBBRrNhuGTGeWAbZS/jh/YfzZMDwDJzBeBgNVHSAE\r\nVzBVMFMGByqDDgMDAgQwSDAhBggrBgEFBQcCARYVaHR0cDovL3BraS5nb3Yua3ovY3BzMCMGCCsG\r\nAQUFBwICMBcMFWh0dHA6Ly9wa2kuZ292Lmt6L2NwczBWBgNVHR8ETzBNMEugSaBHhiFodHRwOi8v\r\nY3JsLnBraS5nb3Yua3ovbmNhX3JzYS5jcmyGImh0dHA6Ly9jcmwxLnBraS5nb3Yua3ovbmNhX3Jz\r\nYS5jcmwwWgYDVR0uBFMwUTBPoE2gS4YjaHR0cDovL2NybC5wa2kuZ292Lmt6L25jYV9kX3JzYS5j\r\ncmyGJGh0dHA6Ly9jcmwxLnBraS5nb3Yua3ovbmNhX2RfcnNhLmNybDBiBggrBgEFBQcBAQRWMFQw\r\nLgYIKwYBBQUHMAKGImh0dHA6Ly9wa2kuZ292Lmt6L2NlcnQvbmNhX3JzYS5jZXIwIgYIKwYBBQUH\r\nMAGGFmh0dHA6Ly9vY3NwLnBraS5nb3Yua3owDQYJKoZIhvcNAQELBQADggIBACy0Lxj0D/q3SwUz\r\n0X9BICyKPw/U6sXmedqUcrghzZuT9ojnUp9w7g4ndZOKTRRxQyLiUYb9neJ3SGVuF/XYcs7Ovrp5\r\nRGNNHuVUR8bQz9cbWd/O2qRUY6qlg4ZSjYsjFYaQm8o+uO56PuqWG125O7XNUdAUHNBc2hUrrngG\r\nKU0FKxlBygxLpvTf4I9q3QA0PJ6MnHrUKlor4sRGar4hMJCbrxeMG4pv3Jx/r9fsKy7f+yZeQo3T\r\n4XAIXmUTXF8UC3HtIroxAP6yEoEhG76oS3qvYc1K/krI48ju5VYxmzEabNqRhiiEBpocIwCqFLLo\r\n9x3CKuUkuA7pwEib4YcCNxCTucCtd9x8dGgZRNffJV4de/Aja/VP84q8rxmcyogbUQzvPb+2/zKR\r\nh6cxYxnRsuL4wWUV+fxp/usy0mJMboQF7IcRFe1fXosU0RWYmKHITOCDbs0NKxTn7TSxEKMYdJN6\r\nYngCmKlmwR/+AfxhN1QMSQpU/m8Glwl+f5wZIL5MQJVhrrWIteh0tnb+OuDQHz4g2vmD2xq5jUQD\r\nFIrjXOdy4zToqM6tirt3nGDsblWgcgsPac50FLT1+um7W26UsmtZ9/wXvkxYC9kL5gUX53VD/bcK\r\nki8fogjrNoYEZiORRqmwvZ5EVe4w3Hfb7YCnc3NzhhIg6hqmzumXNCgLCt2q\r\n</ds:X509Certificate>\r\n</ds:X509Data>\r\n</ds:KeyInfo>\r\n</ds:Signature></root>"
	}
}
```

### Параметры запроса

- `xml` - Данные в формате XML, которые надо подписать
- `verifyOcsp` - *(необязательно)* Провести проверку на отозванность через OCSP.
- `verifyCrl` - *(необязательно)* Провести проверку на отозванность через CRL.


## Ответ

### Пример ответа

```json
{
    "result": {
        "valid": true,
        "cert": {
            "valid": true,
            "notAfter": "2019-08-22 18:11:36",
            "chain": [
                {
                    "valid": true,
                    "notAfter": "2019-08-22 18:11:36",
                    "keyUsage": "AUTH",
                    "serialNumber": "122684438670642568061334282296011886211357830154",
                    "subject": {
                        "lastName": "ТЕСТОВИЧ",
                        "country": "KZ",
                        "commonName": "ТЕСТОВ ТЕСТ",
                        "gender": "",
                        "surname": "ТЕСТОВ",
                        "locality": "АЛМАТЫ",
                        "dn": "CN=ТЕСТОВ ТЕСТ,SURNAME=ТЕСТОВ,SERIALNUMBER=IIN123456789011,C=KZ,L=АЛМАТЫ,S=АЛМАТЫ,G=ТЕСТОВИЧ",
                        "state": "АЛМАТЫ",
                        "birthDate": "12-34-56",
                        "iin": "123456789011"
                    },
                    "signAlg": "SHA256WithRSAEncryption",
                    "sign": "LLQvGPQP+rdLBTPRf0EgLIo/D9TqxeZ52pRyuCHNm5P2iOdSn3DuDid1k4pNFHFDIuJRhv2d4ndIZW4X9dhyzs6+unlEY00e5VRHxtDP1xtZ387apFRjqqWDhlKNiyMVhpCbyj647no+6pYbXbk7tc1R0BQc0FzaFSuueAYpTQUrGUHKDEum9N/gj2rdADQ8noycetQqWivixEZqviEwkJuvF4wbim/cnH+v1+wrLt/7Jl5CjdPhcAheZRNcXxQLce0iujEA/rISgSEbvqhLeq9hzUr+SsjjyO7lVjGbMRps2pGGKIQGmhwjAKoUsuj3HcIq5SS4DunASJvhhwI3EJO5wK133Hx0aBlE198lXh178CNr9U/ziryvGZzKiBtRDO89v7b/MpGHpzFjGdGy4vjBZRX5/Gn+6zLSYkxuhAXshxEV7V9eixTRFZiYochM4INuzQ0rFOftNLEQoxh0k3pieAKYqWbBH/4B/GE3VAxJClT+bwaXCX5/nBkgvkxAlWGutYi16HS2dv464NAfPiDa+YPbGrmNRAMUiuNc53LjNOiozq2Ku3ecYOxuVaByCw9pznQUtPX66btbbpSya1n3/Be+TFgL2QvmBRfndUP9twqSLx+iCOs2hgRmI5FGqbC9nkRV7jDcd9vtgKdzc3OGEiDqGqbO6Zc0KAsK3ao=",
                    "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtKWLOJf9qCqA6EO/SVtiMuPZ8q3Sg2RjO0dWXqKQRP7BWhIyMucMv+WmpRs8RuJ987Hm3B/JszSdiPrmtA9BpIERKphRwp3n4QR6pfLUBEp+5QNetNsv+dbiPcefWCzgJZCqEZVbPvSkiFH20y13YQ2FhEBUp4lLOqydBD2CsDVoTusvLanEgR+AdziJPq2+iXwhttpNPShKRTXGbGkxUa4P7YMUCUqWstR7svLaJqxKDMhaR7MpEt56a2pfntm5oFxKNFoBQjRXKbiBNIKciMRAeznjezv9ZA98WzWPIMuWzi38fPW5X7IVqa7ZbAFWvZIHWJmrl57uKGBNd9EUewIDAQAB",
                    "issuer": {
                        "commonName": "ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
                        "country": "KZ",
                        "dn": "C=KZ,CN=ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)"
                    },
                    "notBefore": "2018-08-22 18:11:36",
                    "keyUser": [
                        "INDIVIDUAL"
                    ]
                },
                {
                    "valid": true,
                    "notAfter": "2025-06-25 10:26:36",
                    "keyUsage": "UNKNOWN",
                    "serialNumber": "305229402244045643062022638026814839687773800430",
                    "subject": {
                        "commonName": "ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
                        "country": "KZ",
                        "dn": "C=KZ,CN=ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)"
                    },
                    "signAlg": "SHA256WithRSAEncryption",
                    "sign": "Ddx8a01Z3HOMbVrvQcQZVS8yzIJRCYaEMbLYoWyDsMpoMB68jchHJ5ovGE4Qd2J/ihyzGe4+vRrtA9zKy98VrrJO+WPZoibepdj8tr4uU6WM0KiPcQTOMVLJJSEC/U0C+eWa5j6OpbRnQuQuJXZA/5eUJ5WMCZJae6vlIuVsNR7fnJgHdcYnxSXvgLHt9YkN1PZOuCHmFNI09U/isiUs004dRxe5a0AFVXwWy2v3DcL6K6Uq9ptBolaEy6mwYPXuVFRaZMyRFAL30vrIiU7k8dHGsNzY0Y2f+cwlfOtW049GIkTYmE6DV3zkNAKs0AhS1aDZq/epsV2R5c8Je33EF4E+SkcDuAK1h/xr8czx3Bi+tdoltlgqCHXT/dXX68L3ioelOTsLTfPYxVO2Itm3PGqfc3V5kyvz0Grgc9M8yNAKDuncn1qwzioy9ZgPiWNTqmm9M/KjyVTT3qoDmPsfFHkGwiEqUBWEyCSWEjDALbDiIvI2PuOx48O3coB7dfW0s2ClhfOvLrUKDMxVJJ1yJXOZb78ckfLlnllJrwCkPS7a43K6zK8ba5zi63TcAASCRpR+CPcEnChHPh4s8dCPu9oexo4/0ZdXbMDesYwylndleu2WGcFaxiSWix9Jpas50AH0qnijfDlGlVFZSPcBENrdb3M7iNgWIbzavtKG7nQ=",
                    "publicKey": "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEArlvMc9X0xktVYz/TmbbFKzkNnX5ZDqRN748xjaE5v/nbv56n0h2+UDG3NCW9uvpDojwmD0Vg5YVB11ASoyh6KmxpPy9vsXnr1UingzcEuz3X4vXElWFGGiWCidauN32fc3TqkS+CJc4PYHGVak8ifFU29MtEi4OOw7rko57Jl4HMuc+FRoQsb1Nw+e9m312i5xoLzxxpL1EVE1EObTfJ+4H4skT7me5f6zJwxUuQAojQAe7ghx2XoOKSo/aX8wHcFJydDsLPNMIqfmm6rpHTpW45X0QuEJpvwlIXa/Ovh5C4cActfPrX8s3ba8Ug0YwZvwuxLhKQIDP/fSxidMKwOVwAyJCLYV30dtwEwcSVfBCyg3/k5aWofgK2ZHnm6Z4+9sLAJGZkXXabrokr8nr0IOPnlioQMuGqYif4GFUy/VlWlanLcOSvi2sfdqDJeQ4XSOP1tazhifmBITKNwtZFv1e95i0OIH0mkGftZEax3BJ3Oi2Gk3xVxP0s5ImHgkrLztYiT2QuDC5MqyZ7NX0Q4k9dEYEBqbEAYvE1M8V7QCvqM+pBfS0MmMasgGrQsb22kYJ6MS4qQVK6IvOJT/IKqOUNT5HzFRYwqygzpHQQvHlkt8PGVcwK9m1F6WwbKecv2S2NuR+WQPI2aU2Qf3hL6Ooi0C4wH4noFbkf34+qXVUCAwEAAQ==",
                    "issuer": {
                        "country": "KZ",
                        "commonName": "НЕГІЗГІ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
                        "organization": "РМК «МЕМЛЕКЕТТІК ТЕХНИКАЛЫҚ ҚЫЗМЕТ»",
                        "dn": "CN=НЕГІЗГІ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA),O=РМК «МЕМЛЕКЕТТІК ТЕХНИКАЛЫҚ ҚЫЗМЕТ»,C=KZ"
                    },
                    "notBefore": "2018-08-08 10:26:36",
                    "keyUser": []
                },
                {
                    "valid": true,
                    "notAfter": "2025-07-27 11:22:53",
                    "keyUsage": "UNKNOWN",
                    "serialNumber": "483236974449879461588506755984708205979682368059",
                    "subject": {
                        "country": "KZ",
                        "commonName": "НЕГІЗГІ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
                        "organization": "РМК «МЕМЛЕКЕТТІК ТЕХНИКАЛЫҚ ҚЫЗМЕТ»",
                        "dn": "CN=НЕГІЗГІ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA),O=РМК «МЕМЛЕКЕТТІК ТЕХНИКАЛЫҚ ҚЫЗМЕТ»,C=KZ"
                    },
                    "signAlg": "SHA256WithRSAEncryption",
                    "sign": "WkjJgzPV7GDmpd9N38h9o8M+t/rpeJvEgReNzsT10fwKV3exmxwoiyFXAOjHf+lAVITj2kdcRUiD1Gr7vaWUCBpXeAMpKukfov31jG0q9Er0mWfs9iDaow66AwPa1zqh9kW8/xuEQVz/iI9Gl6F9q8bdEvFyb4xuD7TqFlqLIO7Z6oJlJZatIEACKFTTjd54d8+7/MJ3OVxfhe8ozMfXywQ3C7sjM9uSKzkgZZHmjpWTGljfAnjqP3ACLMEx1bxW+sKjjzrlRcML5iKDt8HJ2N/MtU7putMoMAAd5x79RPvZSV9h9fW4/qdnfwwneieh/w+KWJN+X6b9wMVWTh24CVif+NyCYqQFwr1RYkKtcBzSiRmmXh/rmPm4jmefGg6jNycbF66OcVI/HxnIH0zAZaVGM6eSLrbdiXjr9OSG2nt+qIkSsytjxCPuoLzLkJxYexZbDWcEDd9G2GpvYgIyFo8+ycJpdLJBa5wPUn8brd4rWnadmzjptt8LYWxrG8XcnJc2N78emf7MFP4LXyHypht+v+bWWO9uze0l6p4Z2JcPg3CFvEftAsKBkXCusE7nOgXq8kVTNBTmB3PfUcq3Ss3+QPwdziMsW8SLeEnO5Rcec7HAOrI2t24QY9htRREwUAGjAJQfDRrjEP+zzoazILyKPn6tGXhRcDybTrqqjRQ=",
                    "publicKey": "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAlqLdP8Z8G8sr5fWi+lp8r2fqRe2NLsiuHntkNTqu11bnY7h+q5LlAbTVJiPfFRchqegg64shKdqNy+9rPW3Swq2hwAQQ2HrAwoTaeKPgh3tusVxhRAyEmq2vVwGWmuiciuDUSr45hMYMEGb8SwocFdW17gx6b+MYivcQ4UQv+Jgmt1r00tyBd5qcavUISM/xmYqNfj+oxYCpuHto+DSYmkD1LCgObSe8JJ2BMYH1ShF7GoLprvnEh1BhxhYQj9zHqnlgee3j96IeAT+nfB0mOgK06pzMqXUAwJ0ip1F5zSzDhRfE/6Y9GLI5O3mPNeU1LMH70DTPoPHwFg4+Cvo9UGkuYO5ZQBsEPZIAXalW8f11u5O5wA5wQPv/v9Q1NCfjMsu3UiGG7pNemOkOatzIn22aP4ys8Zfrq+UfgDuRsQcevwmSEnhcyQ9CbZv1T28wTHU8WhF3vwB/f93Z2rJorvJuHuJFk/aBPckeQW3eDxgks3L1dZM2nIIeYrUkE3oey223eVQQa/YWAfOF8svVt2HbtQPjhGGj6858xvTYi4FErZA2P5nojgJ7jSdSMWiu8dLt/KjNHTDEIPaYCKvt0qtgS36gV0QCbXGyrSTNIXrXhCeX71SYvJbjPMYmSH94tY6KERSpSd5ixVaVYKbZVbyww8ZTD1PBeL42esSCaZsCAwEAAQ==",
                    "issuer": {
                        "country": "KZ",
                        "commonName": "НЕГІЗГІ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
                        "organization": "РМК «МЕМЛЕКЕТТІК ТЕХНИКАЛЫҚ ҚЫЗМЕТ»",
                        "dn": "CN=НЕГІЗГІ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA),O=РМК «МЕМЛЕКЕТТІК ТЕХНИКАЛЫҚ ҚЫЗМЕТ»,C=KZ"
                    },
                    "notBefore": "2015-07-27 11:22:53",
                    "keyUser": []
                }
            ],
            "keyUsage": "AUTH",
            "serialNumber": "122684438670642568061334282296011886211357830154",
            "subject": {
                "lastName": "ТЕСТОВИЧ",
                "country": "KZ",
                "commonName": "ТЕСТОВ ТЕСТ",
                "gender": "",
                "surname": "ТЕСТОВ",
                "locality": "АЛМАТЫ",
                "dn": "CN=ТЕСТОВ ТЕСТ,SURNAME=ТЕСТОВ,SERIALNUMBER=IIN123456789011,C=KZ,L=АЛМАТЫ,S=АЛМАТЫ,G=ТЕСТОВИЧ",
                "state": "АЛМАТЫ",
                "birthDate": "12-34-56",
                "iin": "123456789011"
            },
            "signAlg": "SHA256WithRSAEncryption",
            "sign": "LLQvGPQP+rdLBTPRf0EgLIo/D9TqxeZ52pRyuCHNm5P2iOdSn3DuDid1k4pNFHFDIuJRhv2d4ndIZW4X9dhyzs6+unlEY00e5VRHxtDP1xtZ387apFRjqqWDhlKNiyMVhpCbyj647no+6pYbXbk7tc1R0BQc0FzaFSuueAYpTQUrGUHKDEum9N/gj2rdADQ8noycetQqWivixEZqviEwkJuvF4wbim/cnH+v1+wrLt/7Jl5CjdPhcAheZRNcXxQLce0iujEA/rISgSEbvqhLeq9hzUr+SsjjyO7lVjGbMRps2pGGKIQGmhwjAKoUsuj3HcIq5SS4DunASJvhhwI3EJO5wK133Hx0aBlE198lXh178CNr9U/ziryvGZzKiBtRDO89v7b/MpGHpzFjGdGy4vjBZRX5/Gn+6zLSYkxuhAXshxEV7V9eixTRFZiYochM4INuzQ0rFOftNLEQoxh0k3pieAKYqWbBH/4B/GE3VAxJClT+bwaXCX5/nBkgvkxAlWGutYi16HS2dv464NAfPiDa+YPbGrmNRAMUiuNc53LjNOiozq2Ku3ecYOxuVaByCw9pznQUtPX66btbbpSya1n3/Be+TFgL2QvmBRfndUP9twqSLx+iCOs2hgRmI5FGqbC9nkRV7jDcd9vtgKdzc3OGEiDqGqbO6Zc0KAsK3ao=",
            "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtKWLOJf9qCqA6EO/SVtiMuPZ8q3Sg2RjO0dWXqKQRP7BWhIyMucMv+WmpRs8RuJ987Hm3B/JszSdiPrmtA9BpIERKphRwp3n4QR6pfLUBEp+5QNetNsv+dbiPcefWCzgJZCqEZVbPvSkiFH20y13YQ2FhEBUp4lLOqydBD2CsDVoTusvLanEgR+AdziJPq2+iXwhttpNPShKRTXGbGkxUa4P7YMUCUqWstR7svLaJqxKDMhaR7MpEt56a2pfntm5oFxKNFoBQjRXKbiBNIKciMRAeznjezv9ZA98WzWPIMuWzi38fPW5X7IVqa7ZbAFWvZIHWJmrl57uKGBNd9EUewIDAQAB",
            "issuer": {
                "commonName": "ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
                "country": "KZ",
                "dn": "C=KZ,CN=ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)"
            },
            "notBefore": "2018-08-22 18:11:36",
            "keyUser": [
                "INDIVIDUAL"
            ]
        }
    },
    "message": "",
    "status": 0
}
```

### Параметры ответа

- `valid` - Является ли подпись валидной?
- `cert` - Информация о сертификате. Параметры сертификата, Вы можете посмотреть у метода [X509.info](02_X509.info.md)