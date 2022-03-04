package io.ketill.glfw;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to a {@link GlfwJoystickSeeker} it should use a
 * different relative base path when loading GUID resources. By default,
 * the base path is the package in which the GLFW joystick seeker resides.
 *
 * @see #ROOT
 * @see GlfwJoystickSeeker#loadJsonGuids(String)
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GuidResourcePath {

    /**
     * Represents the root package.
     */
    String ROOT = "";

    /**
     * @return the new path to use. This <i>must not</i> end with a forward
     * slash character ("/").
     */
    String value();

}
