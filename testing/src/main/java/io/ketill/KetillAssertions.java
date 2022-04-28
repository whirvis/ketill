package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class KetillAssertions {

    /**
     * Asserts that a state object is owned by an I/O feature. For this
     * assertion to pass, the following must be true:
     * <pre>
     *     state == device.getState(feature)
     * </pre>
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

        if (state != device.getState(feature)) {
            String msg = "feature with ID \"" + feature.id + "\"";
            msg += " does not own provided state";
            throw new AssertionError(msg);
        }
    }

}
