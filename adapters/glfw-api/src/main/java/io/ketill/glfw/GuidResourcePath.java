package io.ketill.glfw;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When this annotation is present, it signals to a {@link GlfwJoystickSeeker}
 * that it should use a different base path when loading GUID resources. By
 * default, GLFW joystick seekers use the package they reside in as the base
 * path. This is done to prevent possible filename conflicts.
 *
 * @see #ROOT
 * @see GlfwJoystickSeeker#loadJsonGuids(String)
 */
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
