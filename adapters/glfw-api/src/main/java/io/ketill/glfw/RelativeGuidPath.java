package io.ketill.glfw;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to a {@link GlfwJoystickSeeker} what relative base
 * path it should use when loading GUID resources. By default, the base path
 * is the package in which the GLFW joystick seeker class resides.
 *
 * @see #ROOT
 * @see GlfwJoystickSeeker#loadJsonGuids(String)
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RelativeGuidPath {

    /**
     * Represents the root package.
     */
    String ROOT = "/";

    /**
     * @return the relative base path to use. This <i>must</i> begin and
     * end with a forward slash character ("/").
     */
    String value();

}
