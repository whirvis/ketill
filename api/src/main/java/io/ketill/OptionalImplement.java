package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals that a method need not be implemented for a class
 * to properly function. It is usually present on <i>callback methods</i>,
 * such as {@link IoDevice#onAddFeature(IoFeature)}.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface OptionalImplement {
    /* I'm a little teapot! */
}
