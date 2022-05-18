package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoDeviceEvent;
import io.ketill.IoFeature;
import io.ketill.IoFeatureEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The base for pressable {@link IoFeature} events.
 */
public abstract class PressableIoFeatureEvent extends IoFeatureEvent {

    /**
     * @param device  the device which emitted this event.
     * @param feature the feature which triggered this event.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are {@code null}.
     */
    public PressableIoFeatureEvent(@NotNull IoDevice device,
                                   @NotNull IoFeature<?, ?> feature) {
        super(device, feature);
    }

}
