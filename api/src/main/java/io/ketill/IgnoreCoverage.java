package io.ketill;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * When present, signals to the IDE it should not include the annotated
 * element in coverage.
 * <p>
 * <b>Enabling the Annotation</b>
 * <ul>
 *     <li><b>Eclipse:</b> Unknown.</li>
 *     <li><b>IntelliJ IDEA:</b> Go to Settings / Preferences → Build,
 *     Execution, Deployment → Coverage.</li>
 *     <li><b>Apache NetBeans:</b> Unknown.</li>
 * </ul>
 * <p>
 * <b>Visibility:</b> This annotation is {@code package-private} as it is
 * not meant for use outside unit testing the API.
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@interface IgnoreCoverage {
    /* I'm a little teapot! */
}