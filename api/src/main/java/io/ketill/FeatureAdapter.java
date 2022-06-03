package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a method is used as an adapter for an
 * {@link IoFeature}. The purpose of these methods is to take the
 * data an adapter has fetched from an I/O device and update the
 * state of its assigned feature accordingly.
 * <p>
 * This can also occur in the opposite direction, where the state
 * of an assigned feature is used to communicate information to the
 * device. For example, the vibration strength of a rumble motor.
 * <p>
 * <b>Note:</b> Use of this annotation is optional. However, it is
 * recommended as it improves code readability.
 *
 * @see StateUpdater
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface FeatureAdapter {
}
