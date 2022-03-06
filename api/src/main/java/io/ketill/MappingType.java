package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When present, indicates a type is used solely for mapping a feature in a
 * {@link MappedFeatureRegistry}. These exist for features that require more
 * than one data field to map (e.g., an analog stick). Use of this annotation
 * is optional, but recommended as it improves code readability.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MappingType {
}
