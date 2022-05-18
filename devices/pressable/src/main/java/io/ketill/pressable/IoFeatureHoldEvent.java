package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when a pressable {@link IoFeature} is held down.
 */
public class IoFeatureHoldEvent extends PressableIoFeatureEvent {

    /**
     * @param device  the device which emitted this event.
     * @param feature the feature which triggered this event.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are {@code null}.
     */
    public IoFeatureHoldEvent(@NotNull IoDevice device,
                              @NotNull IoFeature<?, ?> feature) {
        super(device, feature);
    }

}
