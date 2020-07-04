# node.info

Возвращает информацию о сервере


# Запрос

### Параметры запроса

Метод не содержит параметров запроса

### Пример запроса

```json
{
    "version": "2.0",
    "method": "node.info"
}
```

## Ответ

### Пример ответа
```json
{
    "dateTime": "2020-07-04 14:33:25",
    "timezone": "Asia/Almaty",
    "kalkanVersion": "0.6",
    "name": "NCANode v2.0.0",
    "version": "2.0.0",
    "status": 0,
    "message": ""
}
```

### Параметры ответа

- `dateTime` - Время на сервере NCANode
- `timezone` - Временная зона на сервере NCANode
- `name` - Название
- `kalkanVersion` - Версия библиотеки Kalkan-Crypt
- `version` - Версия
- `status` - Статус выполнения (Если 0 то всё ОК)
- `message` - Сообщение (Если всё ок, то будет пустым)