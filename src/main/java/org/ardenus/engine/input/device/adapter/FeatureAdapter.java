package org.ardenus.engine.input.device.adapter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to a {@link DeviceAdapter} that a method should be
 * registered as an input feature adapter. Feature adapters are used to
 * implement the functionality of an external feature. Some examples of a
 * feature would be rumble, lighting, etc.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureAdapter {
	/* I'm a little teapot! */
}
