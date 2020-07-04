package kz.ncanode.api.core.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InputField {
    boolean required() default false;
    String requiredWith() default "";
    String requiredWithout() default "";
}
