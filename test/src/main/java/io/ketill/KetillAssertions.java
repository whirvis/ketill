package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class contains special assertions specifically for testing Ketill.
 */
public final class KetillAssertions {

    private KetillAssertions() {
        /* prevent instantiation */
    }

    /**
     * Asserts than I/O device supports all features registered to it when
     * this method is called.
     *
     * @param device      the device to check whose features to check.
     * @param unsupported unsupported features that are registered to
     *                    the device. Take note that the test will fail
     *                    if a feature is not registered to {@code device}
     *                    or actually is supported.
     * @throws NullPointerException if {@code device}, {@code unsupported}
     *                              are {@code null}; if {@code unsupported}
     *                              contains a {@code null} element.
     * @throws AssertionError       if an element of {@code unsupported}
     *                              is not registered to {@code device};
     *                              if a feature marked as unsupported is
     *                              actually supported by {@code device};
     *                              if {@code device} does not support a
     *                              feature not marked as unsupported.
     */
    public static void assertAllFeaturesSupported(@NotNull IoDevice device,
                                                  @NotNull IoFeature<?, ?> @NotNull ... unsupported) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(unsupported, "unsupported cannot be null");

        for (IoFeature<?, ?> feature : unsupported) {
            Objects.requireNonNull(feature, "unsupported cannot contain null");

            /*
             * It would not make sense to pass an unsupported feature which
             * isn't registered to the device in the first place. As such,
             * assume this was a user mistake and throw an exception.
             */
            if (!device.isFeatureRegistered(feature)) {
                String msg = "feature with ID \"" + feature.getId() + "\"";
                msg += " marked as unsupported but not registered";
                throw new AssertionError(msg);
            }
        }

        List<IoFeature<?, ?>> unsupportedList = Arrays.asList(unsupported);
        for (RegisteredIoFeature<?, ?, ?> registered :
                device.getFeatureRegistrations()) {
            IoFeature<?, ?> feature = registered.feature;

            if (unsupportedList.contains(feature)) {
                /*
                 * It would not make sense to pass an unsupported feature
                 * which is supported by the device. As such, assume this
                 * was a user mistake and throw an exception.
                 */
                if (device.isFeatureSupported(feature)) {
                    String msg = "feature with ID \"" + feature.getId() + "\"";
                    msg += " supported when marked as unsupported";
                    throw new AssertionError(msg);
                } else {
                    continue;
                }
            }

            if (!device.isFeatureSupported(feature)) {
                String msg = "feature with ID \"" + feature.getId() + "\"";
                msg += " not supported as expected";
                throw new AssertionError(msg);
            }
        }
    }

    /**
     * Asserts that a state object is owned by an I/O feature.<br>
     * For this assertion to pass, the following must be true:
     * <pre>
     *     device.getFeature(state) == feature
     * </pre>
     * {@link IoDevice#getState(IoFeature)} is not used here as it returns
     * the <i>container</i> state of an I/O feature. Using the inverse of
     * this method enables this assertion to work for both internal and
     * container states.
     *
     * @param device  the device which owns {@code feature}.
     * @param state   the state which {@code feature} should own.
     * @param feature the feature which should own {@code state}.
     * @throws NullPointerException if {@code device}, {@code state}, or
     *                              {@code feature} are {@code null}.
     * @throws AssertionError       if {@code feature} does not own the
     *                              provided {@code state} instance.
     */
    public static void assertFeatureOwnsState(@NotNull IoDevice device,
                                              @NotNull Object state,
                                              @NotNull IoFeature<?, ?> feature) {
        Objects.requireNonNull(device, "device cannot be null");
        Objects.requireNonNull(state, "state cannot be null");
        Objects.requireNonNull(feature, "feature cannot be null");

        if (device.getFeature(state) != feature) {
            String msg = "feature with ID \"" + feature.getId() + "\"";
            msg += " does not own provided state";
            throw new AssertionError(msg);
        }
    }

    /**
     * Asserts that a class instance implements {@code toString()}.<br>
     * For this assertion to pass, the following must be true:
     * <ul>
     *     <li>The class must override {@code toString()}.</li>
     *     <li>It must not return {@link Object#toString()}.</li>
     * </ul>
     * An object instance is required so a call to {@code toString()} can
     * be made. The result of this method call will be used to determine
     * if the requirements for the contract are met.
     *
     * @param clazz the class of which to verify.
     * @param obj   the object instance to verify with.
     * @param <T>   the object type.
     * @throws NullPointerException          if {@code clazz} or {@code obj}
     *                                       are {@code null}.
     * @throws UnsupportedOperationException if the argument for {@code clazz}
     *                                       is equal to {@code Object.class}.
     * @throws AssertionError                if {@code clazz} does not
     *                                       override {@code toString()};
     *                                       if {@code obj.toString()} returns
     *                                       {@code Object.toString()}.
     */
    /* @formatter:off */
    public static <T> void
            assertImplementsToString(@NotNull Class<T> clazz,
                                     @NotNull T obj) {
        Objects.requireNonNull(clazz, "clazz cannot be null");
        Objects.requireNonNull(obj, "obj cannot be null");

        if (clazz == Object.class) {
            String msg = "cannot verify Object";
            throw new UnsupportedOperationException(msg);
        }

        try {
            Method toString = clazz.getMethod("toString");
            if (toString.getDeclaringClass() != clazz) {
                String msg = clazz.getName() + " must override toString()";
                throw new AssertionError(msg);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        String clazzName = obj.getClass().getName();
        String hashCode = Integer.toHexString(obj.hashCode());
        String objStr = clazzName + "@" + hashCode;

        if (obj.toString().equals(objStr)) {
            String msg = "toString() must not return Object.toString()";
            throw new AssertionError(msg);
        }
    }
    /* @formatter:on */

}
