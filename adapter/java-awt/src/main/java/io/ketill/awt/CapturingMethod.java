package io.ketill.awt;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is used solely for capturing AWT
 * devices into instantiated I/O devices.
 * <p>
 * <b>Note:</b> Use of this annotation is optional. However, it is
 * recommended as it improves code readability.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface CapturingMethod {
}
