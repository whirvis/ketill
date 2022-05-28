package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * This can be used in unit tests to ensure the contract specified by the
 * Ketill API for the {@code toString()} method is met for a class. The
 * contract is as follows:
 * <ul>
 *     <li>The class must override {@code toString()}.</li>
 *     <li>It must not return {@link Object#toString()}.</li>
 * </ul>
 * <p>
 * Using a {@code ToStringVerifier} is as follows:
 * <pre>
 * ToStringVerifier.forClass(Thing.class, new Thing()).verify();
 * </pre>
 * An object instance must be provided so a call to {@code toString()} can
 * be made by the verifier. The result of this method call will be used to
 * determine if the requirements for the contract are met.
 */
public class ToStringVerifier<T> {

    private static @NotNull String superToString(@NotNull Object obj) {
        String clazzName = obj.getClass().getName();
        String hashCode = Integer.toHexString(obj.hashCode());
        return clazzName + "@" + hashCode;
    }

    /**
     * Factory method for a {@code toString()} verifier. This method exists
     * only to match the pattern of the {@code EqualsVerifier} API used by
     * Ketill.
     *
     * @param clazz the class of which to verify.
     * @param obj   the object instance to verify with.
     * @param <O>   the object type.
     * @return a {@code ToStringVerifier} for {@code clazz}.
     * @throws NullPointerException          if {@code clazz} or {@code obj}
     *                                       are {@code null}.
     * @throws UnsupportedOperationException if the argument for {@code clazz}
     *                                       is equal to {@code Object.class}.
     */
    /* @formatter:off */
    public static <O> @NotNull ToStringVerifier<O>
            forClass(@NotNull Class<O> clazz, @NotNull O obj) {
        Objects.requireNonNull(clazz, "clazz cannot be null");
        Objects.requireNonNull(obj, "obj cannot be null");
        if (clazz == Object.class) {
            throw new UnsupportedOperationException("cannot verify Object");
        }
        return new ToStringVerifier<>(clazz, obj);
    }
    /* @formatter:on */

    private final Class<T> clazz;
    private final T obj;

    private ToStringVerifier(@NotNull Class<T> clazz, @NotNull T obj) {
        this.clazz = clazz;
        this.obj = obj;
    }

    /**
     * Performs the verification of the contract for {@code toString()} as
     * specified in the definition for this class.
     *
     * @throws AssertionError if the contract is not met.
     */
    public void verify() {
        try {
            Method toString = clazz.getMethod("toString");
            if (toString.getDeclaringClass() != clazz) {
                String msg = clazz.getName() + " must override toString()";
                throw new AssertionError(msg);
            }

            if (obj.toString().equals(superToString(obj))) {
                String msg = "toString() must not return super.toString()";
                throw new AssertionError(msg);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
