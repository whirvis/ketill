package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is a <i>shorthand</i> for mapping an
 * {@link IoFeature} in a {@link MappedFeatureRegistry}. The usage of
 * these methods can provide parameter checks and improve the overall
 * readability of feature mapping.
 * <p>
 * <b>Note:</b> Use of this annotation is optional. However, it is
 * recommended as it improves code readability.
 *
 * @see MappedFeatureRegistry#mapFeature(IoFeature, Object, StateUpdater)
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingMethod {
}
