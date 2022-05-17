package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when an {@link IoFeature} is unregistered.
 *
 * @see IoDevice#unregisterFeature(IoFeature)
 */
public final class IoFeatureUnregisterEvent extends IoFeatureEvent {

    /**
     * @param device  the device which emitted this event.
     * @param feature the feature which was unregistered.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are {@code null}.
     */
    IoFeatureUnregisterEvent(@NotNull IoDevice device,
                             @NotNull IoFeature<?, ?> feature) {
        super(device, feature);
    }

}
