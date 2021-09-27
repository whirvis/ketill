package org.ardenus.engine.input.device;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When presents, signals to an {@link InputDevice} that it should support an
 * input source of a specific type, as well as its children. The purpose of this
 * annotation is to prevent non-sensical input sources being added to input
 * devices (for example, a keyboard being given a gyroscope input source.)
 * 
 * @see InputDevice#addSource(InputSource)
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(SupportSourcesContainer.class)
public @interface SupportSources {

	public Class<?>[] value();

}
