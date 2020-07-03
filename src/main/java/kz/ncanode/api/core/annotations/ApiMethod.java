package kz.ncanode.api.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ApiMethod {
    String url();
    Class<?> model() default void.class;
}
