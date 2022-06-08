package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a type is used solely for mapping a feature in a
 * {@link MappedFeatureRegistry}. These exist for features that require more
 * than one data point to be mapped to an {@link IoFeature}. For example, an
 * analog stick.
 * <p>
 * <b>Example of using this annotation:</b>
 * <pre>
 * &#64;MappingType
 * class StickMapping {
 *
 *     final int xAxis;
 *     final int yAxis;
 *
 *     StickMapping(int xAxis, int yAxis) {
 *         this.xAxis = xAxis;
 *         this.yAxis = yAxis;
 *     }
 *
 * }
 * </pre>
 * <p>
 * <b>Note:</b> Use of this annotation is optional. However, it is
 * recommended as it improves code readability.
 *
 * @see MappingMethod
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingType {
}
