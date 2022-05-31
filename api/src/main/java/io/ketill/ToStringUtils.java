package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * Utilities for implementing {@code toString()}.
 *
 * @see #getJoiner(Object)
 * @see #getJoiner(String, Object)
 */
public final class ToStringUtils {

    private static final String DELIMETER = ", ";
    private static final String SUFFIX = "]";

    private ToStringUtils() {
        /* prevent instantiation */
    }

    private static String getPrefix(Object obj) {
        return obj.getClass().getSimpleName() + "[";
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
     */
    public static StringJoiner getJoiner(@NotNull Object obj) {
        Objects.requireNonNull(obj, "obj cannot be null");
        return new StringJoiner(DELIMETER, getPrefix(obj), SUFFIX);
    }

    /**
     * Returns a {@link StringJoiner} for a class adding onto the result
     * of it's super class's call to {@code toString()}. <i>This assumes
     * the super class in question used {@link ToStringUtils}, and that
     * the class calling this method extends from said super class</i>.
     * If not, the result shall be undefined.
     * <p>
     * This works by stripping the suffix of the original {@code toString()}
     * call, using the result as the prefix for the new string joiner. The
     * delimeter and suffix shall be the same.
     *
     * @param prev the result of {@code super.toString()}.
     * @param obj  the object being converted to a string.
     * @return the string joiner.
     * @throws NullPointerException if {@code prev} or {@code obj}
     *                              are {@code null}.
     */
    public static StringJoiner getJoiner(@NotNull String prev,
                                         @NotNull Object obj) {
        Objects.requireNonNull(prev, "prev cannot be null");
        Objects.requireNonNull(obj, "obj cannot be null");

        int endIndex = prev.length() - SUFFIX.length();
        String prefix = prev.substring(0, endIndex);
        return new StringJoiner(DELIMETER, prefix, SUFFIX);
    }

}
