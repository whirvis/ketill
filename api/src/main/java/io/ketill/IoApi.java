package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Utilities for the Ketill I/O API.
 */
public final class IoApi {

    /**
     * When present, signals that a method has a default behavior
     * and specifies what that behavior is.
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface DefaultBehavior {

        /**
         * Describes the default behavior of this method.
         *
         * @return the default behavior of this method.
         */
        String value();

    }

    /**
     * When present, signals that a method is shorthand for another,
     * usually of the same name.
     */
    @Documented
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shorthand {
        /* this annotation has no attributes */
    }

    /**
     * When present, signals a component should only be accessed by the
     * given classes. This should only be applied to components with the
     * {@code package private} access modifier.
     * <p>
     * <b>Note:</b> This annotation has zero runtime enforcement, it is
     * purely for documentation.
     */
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Friends {

        /**
         * Returns the intended calling classes.
         *
         * @return the intended calling classes.
         */
        Class<?>[] value();

    }

    private static final String JOINER_DELIMETER = ", ";
    private static final String JOINER_SUFFIX = "]";

    private IoApi() {
        /* prevent instantiation */
    }

    /**
     * Checks that the specified ID is valid, and throws an exception if
     * it is not. For the ID to be valid it must not be {@code null} and
     * must not be empty or contain whitespace.
     *
     * @param id the ID to validate.
     * @return {@code id} if valid.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public static @NotNull String validateId(@NotNull String id) {
        Objects.requireNonNull(id, "id must not be null");
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id must not be empty");
        } else if (!id.matches("\\S+")) {
            throw new IllegalArgumentException("id must not contain whitespace");
        }
        return id;
    }

    /**
     * Returns a {@link StringJoiner} to convert the specified object to
     * a string. The returned joiner shall have been constructed with the
     * following arguments:
     * <ul>
     *     <li>delimeter: {@code ", "}</li>
     *     <li>prefix: {@code obj.getClass().getSimpleName() + "["}</li>
     *     <li>suffix: {@code "]"}</li>
     * </ul>
     *
     * @param obj the object being converted to a string.
     * @return the string joiner.
     * @throws NullPointerException if {@code obj} is {@code null}.
     * @see #getStrJoiner(String, Object)
     */
    public static StringJoiner getStrJoiner(@NotNull Object obj) {
        Objects.requireNonNull(obj, "obj cannot be null");
        String prefix = obj.getClass().getSimpleName() + "[";
        return new StringJoiner(JOINER_DELIMETER, prefix, JOINER_SUFFIX);
    }

    /**
     * Returns a {@link StringJoiner} for an object adding to the result of
     * its super class' call to {@code toString()}. This strips the suffix
     * of the original {@code toString()} call, using the result as the
     * prefix for the new string joiner. The delimeter and suffix shall be
     * the same as before.
     * <p>
     * <b>Requirements</b>
     * <p>
     * It is assumed the super class used {@link #getStrJoiner(Object)},
     * and the class calling this method extends from said super class.
     * If this is not the case, the result shall be undefined.
     *
     * @param prev the result of {@code super.toString()}.
     * @param obj  the object being converted to a string.
     * @return the string joiner.
     * @throws NullPointerException if {@code prev} or {@code obj}
     *                              are {@code null}.
     */
    public static StringJoiner getStrJoiner(@NotNull String prev,
                                            @NotNull Object obj) {
        Objects.requireNonNull(prev, "prev cannot be null");
        Objects.requireNonNull(obj, "obj cannot be null");

        int endIndex = prev.length() - JOINER_SUFFIX.length();
        String prefix = prev.substring(0, endIndex);
        return new StringJoiner(JOINER_DELIMETER, prefix, JOINER_SUFFIX);
    }

}