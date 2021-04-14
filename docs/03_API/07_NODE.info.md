# NODE.info

Возвращает информацию о сервере


# Запрос

### Параметры запроса

Метод не содержит параметров запроса

### Пример запроса

```json
{
	"version": "1.0",
	"method":"NODE.info"
}
```

## Ответ

### Пример ответа
```json
{
    "result": {
        "dateTime": "2018-10-23 00:09:37",
        "timezone": "Asia/Almaty",
        "name": "NCANode v1.0",
        "kalkanVersion": "0.4",
        "version": "1.0"
    },
    "message": "",
    "status": 0
}
```

### Параметры ответа

- `dateTime` - Время на сервере NCANode
- `timezone` - Временная зона на сервере NCANode
- `name` - Название
- `kalkanVersion` - Версия библиотеки Kalkan-Crypt
- `version` - Версия