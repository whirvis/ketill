package com.whirvis.kibasan.feature;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.whirvis.kibasan.InputDevice;

/**
 * When present, signals to {@link InputDevice} that a field should be
 * registered as a device feature during instantiation. These fields must be
 * {@code public} and, their type assignable from {@link DeviceFeature}. They
 * may be either instance or static.
 * 
 * @see InputDevice#addFeature(DeviceFeature)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeaturePresent {
	/* I'm a little teapot! */
}
