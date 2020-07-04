package kz.ncanode.api.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// Аннотация, которая сохраняет URL
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiController {
    String value();
}
