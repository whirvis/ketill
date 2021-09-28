package org.ardenus.engine.input.device;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to {@link InputDevice} that a {@link DeviceFeature}
 * field should be registered automatically. This will be done during
 * instantiation. Both static and instance fields may make use of this
 * annotation.
 * 
 * @see InputDevice#addFeature(DeviceFeature)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeaturePresent {
	/* I'm a little teapot! */
}
