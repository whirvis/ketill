package io.ketill.glfw;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is used solely for wrangling GLFW
 * joysticks into instantiated I/O devices. Use of this annotation is
 * optional, but recommended as it improves code readability.
 *
 * @see GlfwJoystickSeeker#wrangleGuid(String, GlfwJoystickWrangler)
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface WranglerMethod {
}
