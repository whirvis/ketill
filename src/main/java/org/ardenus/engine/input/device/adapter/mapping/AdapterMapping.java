package org.ardenus.engine.input.device.adapter.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;

/**
 * When present, signals to a {@link DeviceAdapter} that a field of type
 * {@link FeatureMapping} should be mapped automatically on instantiation. Both
 * static and instance fields are valid for use with this annotation.
 * 
 * @see DeviceAdapter#map(FeatureMapping)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdapterMapping {
	/* I'm a little teapot! */
}
