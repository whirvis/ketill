package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoFeature;
import io.ketill.IoFeatureEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The base for pressable {@link IoFeature} events.
 */
public abstract class PressableIoFeatureEvent extends IoFeatureEvent {

    PressableIoFeatureEvent(@NotNull IoDevice device,
                            @NotNull IoFeature<?, ?> feature) {
        super(device, feature);
    }

}
