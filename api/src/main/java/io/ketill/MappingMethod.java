package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is a shorthand for mapping a feature
 * in a {@link MappedFeatureRegistry}. Mapping shorthands are generally
 * found in an {@link IoDeviceAdapter}, mainly to pretty up the code to
 * map features. Use of this annotation is optional, but recommended as
 * it improves code readability.
 *
 * @see MappedFeatureRegistry#mapFeature(IoFeature, Object, StateUpdater)
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingMethod {
}
