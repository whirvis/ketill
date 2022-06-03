package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a field contains the state of an
 * {@link IoFeature}.
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
 *     &#64;FeatureState
 *     public final ButtonState
 *             a = this.getState(BUTTON_A),
 *             b = this.getState(BUTTON_B);
 *
 * }
 * </pre>
 * <p>
 * <b>Note:</b> By convention, the field name should match the ID of the
 * feature whose state it contains. However, this is not a requirement.
 * Furthermore, use of this annotation is also optional. However, it is
 * recommended as it improves readability.
 *
 * @see IoDevice#getState(IoFeature)
 * @see FeaturePresent
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureState {
    /* I'm a little teapot! */
}
