package org.ardenus.engine.input.device;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to {@link InputDevice} that a field of type
 * {@link DeviceButton} should be registered automatically on instantiation.
 * Both static and instance fields can be valid for use with this annotation.
 * 
 * @see InputDevice#addButton(DeviceButton)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ButtonPresent {
	/* I'm a little teapot! */
}
