package framework.annotations.mapping;

import framework.configurables.Stringifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Mapping
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IOMapping {
    String value() default "";
    Class<? extends Stringifier> stringifier() default Stringifier.class;
}
