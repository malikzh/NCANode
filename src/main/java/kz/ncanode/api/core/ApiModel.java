package kz.ncanode.api.core;

import kz.ncanode.api.core.annotations.InputField;
import kz.ncanode.api.exceptions.InvalidArgumentException;
import org.json.simple.JSONObject;

import java.lang.reflect.Field;

public abstract class ApiModel extends ApiDependencies {

    protected void afterAccept() throws InvalidArgumentException {}

    public void accept(JSONObject data) throws InvalidArgumentException, IllegalAccessException {
        for (Field field : this.getClass().getFields()) {
            InputType inputType = (InputType) field.get(this);

            if (inputType == null) {
                throw new RuntimeException("Field '" + field.getName() + "' is not initialized");
            }

            // Обработка аннотации
            InputField in = field.getAnnotation(InputField.class);

            if (!data.containsKey(field.getName()) && in.required()) {
                throw new InvalidArgumentException("Field '" + field.getName() + "' is required");
            }

            if (!in.requiredWith().isEmpty() && data.containsKey(in.requiredWith()) && !data.containsKey(field.getName())) {
                throw new InvalidArgumentException("Field '" + field.getName() + "' is required when field '" + in.requiredWith() + "' is specified");
            }

            if (!in.requiredWithout().isEmpty() && !data.containsKey(in.requiredWithout()) && !data.containsKey(field.getName())) {
                throw new InvalidArgumentException("Field '" + field.getName() + "' is required when field '" + in.requiredWithout() + "' is not specified");
            }

            // Валидация
            inputType.setDependencies(getApiVersion(), getApiServiceProvider());
            inputType.input(data.containsKey(field.getName()) ? data.get(field.getName()) : null);
            inputType.validate();
        }

        afterAccept();
    }
}
