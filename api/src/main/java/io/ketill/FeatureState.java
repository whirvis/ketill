package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a field contains the state of an I/O feature.
 * Its value is not set automatically, and must be initialized manually. By
 * convention, the field name should match the ID of the device feature whose
 * state it contains. However, this is not a requirement.
 *
 * @see IoDevice#getState(IoFeature)
 * @see FeaturePresent
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureState {
    /* I'm a little teapot! */
}
