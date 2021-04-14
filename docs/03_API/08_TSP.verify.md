# TSP.verify

Проверяет подпись TSP и возвращает информацию


# Запрос

### Параметры запроса

- `cms` - TSP/CMS данные в формате Base64

### Пример запроса

```json
{
	"version": "1.0",
	"method":"TSP.verify",
	"params": {
		"cms": "MIIF\/AYJK..."
	}
}
```

## Ответ

### Пример ответа

```json
{
    "result": {
        "tspHashAlgorithm": "GOST34311",
        "serialNumber": "849e5212a512d30b24137a63fb9b579f1930aae7",
        "genTime": "2019-03-18 13:40:13",
        "hash": "08a5e8f4b3e3b01fac5e53af166e5e3bc9ce81eb2449aa8d744cd41fff3f72b6",
        "policy": "1.2.398.3.3.2.6.1"
    },
    "message": "",
    "status": 0
}
```

### Параметры ответа

- `tspHashAlgorithm` - Алгоритм хэширования подписи
- `serialNumber` - Серийный номер
- `genTime` - Дата генерации
- `hash` - Хэш
- `policy` - Политика (Подробнее http://root.gov.kz/oid.html )