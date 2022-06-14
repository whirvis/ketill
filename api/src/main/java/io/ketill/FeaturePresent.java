package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates to an {@link IoDevice} it should automatically
 * register a field at construction. These must be {@code public} and their
 * type assignable from {@link IoFeature}. While this is not a requirement,
 * it is also recommended for them to be {@code static}.
 * <p>
 * <b>Example of using this annotation:</b>
 * <pre>
 * &#47;* Controller extends IoDevice *&#47;
 * class NesController extends Controller {
 *
 *     &#64;FeaturePresent
 *     public static final ControllerButton
 *             BUTTON_A = new ControllerButton("a"),
 *             BUTTON_B = new ControllerButton("b");
 *
 * }
 * </pre>
 *
 * @see IoDevice#registerFeature(IoFeature)
 * @see FeatureState
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeaturePresent {
}
