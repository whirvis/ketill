package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates that a field present in an I/O feature state
 * class is updated by the state itself (and thus, should not be modified
 * by the adapter).
 * <p>
 * <b>Note:</b> Use of this annotation is optional. However, it is
 * recommended as it improves code readability.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface AutonomousField {
}
