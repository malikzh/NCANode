# info.pkcs12

Получает информацию по PKCS12 файлу

# Запрос

### Параметры запроса

- `p12` - Закодированный файл c ключом p12, в формате [Base64](https://ru.wikipedia.org/wiki/Base64)
- `password` - Пароль для ключа
- `checkOcsp` - *(необязательно)* Провести проверку на отозванность через OCSP.
- `checkCrl` - *(необязательно)* Провести проверку на отозванность через CRL.
- `alias` - *(необязательно)* Алиас, который можно выбрать

### Пример запроса

```json
{
    "version": "2.0",
    "method": "info.pkcs12",
    "params": {
        "p12":"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",
		"password":"Qwerty12",
		"checkOcsp": true,
		"checkCrl": true,
        "alias": ""
    }
}
```

## Ответ

### Пример ответа
```json
{
    "certificate": {
        "notAfter": "2019-08-22 18:11:36",
        "ocsp": {
            "revokationReason": 4,
            "revokationTime": "2019-02-01 16:37:18",
            "status": "REVOKED"
        },
        "chain": [
            {
                "valid": false,
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
        ],
        "valid": false,
        "keyUsage": "AUTH",
        "alias": "6b361b864c679601b652fe387f61fcd9303c0327",
        "crl": {
            "revokationReason": "",
            "revokationTime": "",
            "revokedBy": "",
            "status": "ACTIVE"
        }
    },
    "message": "",
    "status": 0
}
```

### Параметры ответа

Параметры ответа можно посмотреть у метода [X509.info](02_X509.info.md)