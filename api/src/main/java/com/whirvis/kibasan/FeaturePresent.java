package com.whirvis.kibasan;

import java.lang.annotation.*;

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
