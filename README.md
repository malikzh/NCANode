
![NCANode](docs/NCANode.png)


Приложение-сервер для работы с Электронно Цифровыми Подписью (ЭЦП) РК

---

![License:MIT](https://img.shields.io/badge/license-MIT-green.svg)
![Downloads](https://img.shields.io/github/downloads/malikzh/NCANode/total.svg)
---

Возможности:

- Кроссплатформенный сервер (Windows, Mac OS, Linux)
- Подпись XML данных
- Проверка подписей
- Получение информации о сертификате
- Проверка цепочки сертификатов до КУЦ
- Поддержка OCSP и CRL
- Поддержка RabbitMQ
- Парсинг дополнительной информации из ИИН
- Работа с API посредством JSON
- Поддержка Docker
- Поддержка TSP **NEW!!!**
- Поддержка возможности подписи любых данных в любом формате **NEW!!!**

## Пример

Пример запроса (запрос информации о ключе):

```json
{
	"version": "1.0",
	"method": "PKCS12.info",
	"params": {
		"p12":"MIINkwIBAzCCDU0GCSqGSIb3DQEHAaCCDTJ3i8pKvvVbY...",
		"password":"Qwerty12",
		"verifyOcsp": true
	}
}
```

Пример ответа:

```json
{
    "result": {
        "notAfter": "2019-08-22 18:11:36",
        "ocsp": {
            "revokationReason": 0,
            "revokationTime": null,
            "status": "ACTIVE"
        },
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
                "sign": "LLQvGPQP+rdLBTPRf0EgLIo/D9TqxeZ52pRyuCHN...",
                "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMII...",
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
            ...
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
        "sign": "LLQvGPQP+rdLBTPRf0EgLIo/D9TqxeZ52pRyuCHNm5P2iOdSn3DuDid1k4pNFHFDIuJ...",
        "publicKey": "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtKWLOJf9qCqA6EO/SV...",
        "issuer": {
            "commonName": "ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)",
            "country": "KZ",
            "dn": "C=KZ,CN=ҰЛТТЫҚ КУӘЛАНДЫРУШЫ ОРТАЛЫҚ (RSA)"
        },
        "notBefore": "2018-08-22 18:11:36",
        "keyUser": [
            "INDIVIDUAL"
        ],
        "valid": true,
        "keyUsage": "AUTH"
    },
    "message": "",
    "status": 0
}
```

## Документация

Документацию можно найти на http://ncanode.kz

## Авторы

- **Malik Zharykov** - Initial work

## Благодарности

- ⭐ [@exe-dealer](https://github.com/exe-dealer) - За Dockerfile

## Лицензия

Проект лицензирован под лицензией [MIT](LICENSE)

## Важно!!!

По требованию  АО «НИТ» | НУЦ РК. Библиотеки `kalkancrypt-0.4.jar` и `kalkancrypt_xmldsig-0.3.jar`
Были удалены из репозитория, поэтому для компиляции Вам необходимо подставить библиотеки
из комплекта разработчика (SDK) в директорию `/lib`.
