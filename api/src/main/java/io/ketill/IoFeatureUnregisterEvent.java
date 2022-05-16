package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Emitted by {@link IoDevice} when a feature is unregistered.
 *
 * @see IoDevice#unregisterFeature(IoFeature)
 */
public class IoFeatureUnregisterEvent extends IoDeviceEvent {

    private final @NotNull IoFeature<?, ?> feature;

    /**
     * @param device  the device which emitted this event.
     * @param feature the feature which was unregistered.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are {@code null}.
     */
    IoFeatureUnregisterEvent(@NotNull IoDevice device,
                             @NotNull IoFeature<?, ?> feature) {
        super(device);
        this.feature = Objects.requireNonNull(feature,
                "feature cannot be null");
    }

    /**
     * @return the feature which was unregistered.
     */
    public final @NotNull IoFeature<?, ?> getFeature() {
        return this.feature;
    }

}
