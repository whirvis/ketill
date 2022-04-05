package io.ketill.pressable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates that a field present in an I/O feature state
 * container is updated by a {@link PressableFeatureMonitor}. Use of this
 * annotation is optional, but recommended as it improves code readability.
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MonitorUpdatedField {

    /**
     * @return optional documentation, usually as to why this annotation is
     * present. Useful for preservation in JavaDoc generation. By default, an
     * empty string is returned.
     */
    String value() default "";

}
