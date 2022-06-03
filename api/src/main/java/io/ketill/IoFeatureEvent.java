package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The base for {@link IoFeature} related events emitted by {@link IoDevice}.
 */
public abstract class IoFeatureEvent extends IoDeviceEvent {

    private final IoFeature<?, ?> feature;

    /**
     * Constructs a new {@code IoFeatureEvent}.
     *
     * @param device  the device which emitted this event.
     * @param feature the feature which triggered this event.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are{@code null}.
     */
    public IoFeatureEvent(@NotNull IoDevice device,
                          @NotNull IoFeature<?, ?> feature) {
        super(device);
        this.feature = Objects.requireNonNull(feature,
                "feature cannot be null");
    }

    /**
     * Returns the feature which triggered this event.
     *
     * @return the feature which triggered this event.
     */
    public final @NotNull IoFeature<?, ?> getFeature() {
        return this.feature;
    }

}
