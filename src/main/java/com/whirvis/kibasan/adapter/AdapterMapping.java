package com.whirvis.kibasan.adapter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, signals to {@link DeviceAdapter} that a field should be
 * registered as a feature mapping during instantiation. These fields must be
 * {@code public} and, their type assignable from {@link FeatureMapping}. They
 * may be either instance or static.
 * 
 * @see DeviceAdapter#map(FeatureMapping)
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdapterMapping {
	/* I'm a little teapot! */
}
