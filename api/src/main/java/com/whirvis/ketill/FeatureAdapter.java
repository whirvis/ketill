package com.whirvis.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is used as an I/O feature adapter.
 * Feature adapters are used to fetch input device information and send
 * output data. Use of this annotation is optional, but recommended as
 * it improves code readability.
 *
 * @see StateUpdater
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FeatureAdapter {
}
