package framework.annotations.mapping;

import framework.configurables.OutputConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IOMapping {
    String value() default "";
    Class<? extends OutputConverter> outputConverter() default OutputConverter.class;
}
