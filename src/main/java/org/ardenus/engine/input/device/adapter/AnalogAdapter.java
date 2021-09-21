package org.ardenus.engine.input.device.adapter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to a {@link DeviceAdapter} that a method should be
 * registered as an analog adapter. Analog adapters are used to determine the
 * values of a device adapter's mapped analogs. The requirements for an analog
 * adapter are dependent on the device adapter's implementation.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnalogAdapter {
	/* I'm a little teapot! */
}
