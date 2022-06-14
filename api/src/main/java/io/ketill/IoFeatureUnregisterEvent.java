package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when an {@link IoFeature} is unregistered.
 *
 * @see IoDevice#unregisterFeature(IoFeature)
 */
public final class IoFeatureUnregisterEvent extends IoFeatureEvent {

    IoFeatureUnregisterEvent(@NotNull IoDevice emitter,
                             @NotNull IoFeature<?, ?> feature) {
        super(emitter, feature);
    }

}
