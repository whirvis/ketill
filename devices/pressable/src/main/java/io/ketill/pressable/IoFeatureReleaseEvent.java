package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when a pressable {@link IoFeature} is released.
 */
public class IoFeatureReleaseEvent extends PressableIoFeatureEvent {

    /**
     * @param device  the device which emitted this event.
     * @param feature the feature which triggered this event.
     * @throws NullPointerException if {@code device} or {@code feature}
     *                              are{@code null}.
     */
    public IoFeatureReleaseEvent(@NotNull IoDevice device,
                                 @NotNull IoFeature<?, ?> feature) {
        super(device, feature);
    }

}
