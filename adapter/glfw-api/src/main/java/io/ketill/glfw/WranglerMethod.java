package io.ketill.glfw;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is used solely for wrangling GLFW
 * devices into instantiated I/O devices.
 * <p>
 * <b>Suggestion:</b> Use of this annotation is optional, but recommended
 * as it improves code readability.
 *
 * @see GlfwJoystickWrangler
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface WranglerMethod {
}
