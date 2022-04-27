package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class PressableFeatureEvent {

    public final @NotNull PressableFeatureEventType type;

    /**
     * The I/O device which owns {@code feature}.
     */
    public final @NotNull IoDevice device;

    /**
     * The I/O feature which triggered this event.
     */
    public final @NotNull IoFeature<?, ?> feature;

    /**
     * The current state of {@code feature}. This is the state returned by
     * {@link IoDevice#getState(IoFeature)}.
     */
    public final @NotNull Object state;

    /**
     * {@code true} if {@code feature} is currently being held down (or
     * if {@code feature} <i>was</i> being held down if {@code type} is
     * {@link PressableFeatureEventType#RELEASE}), {@code false} otherwise.
     */
    public final boolean held;

    /**
     * The event data as provided by the monitor. The context of this data
     * is dependent on the monitor that fired this event.
     */
    public final @Nullable Object data;

    /**
     * @param type    the event type.
     * @param device  the device which owns {@code feature}.
     * @param feature the feature which triggered this event.
     * @param held    {@code true} if {@code feature} is currently being
     *                held down (or if {@code feature} <i>was</i> being
     *                held down), {@code false} otherwise.
     * @param data    the monitor event data. The context of this data
     *                is dependent on the monitor that fired this event.
     * @throws NullPointerException  if {@code type}, {@code device}, or
     *                               {@code feature} are {@code null}.
     * @throws IllegalStateException if {@code feature} is not registered
     *                               to {@code device}.
     */
    public PressableFeatureEvent(@NotNull PressableFeatureEventType type,
                                 @NotNull IoDevice device,
                                 @NotNull IoFeature<?, ?> feature,
                                 boolean held, @Nullable Object data) {
        this.type = Objects.requireNonNull(type,
                "type cannot be null");
        this.device = Objects.requireNonNull(device,
                "device cannot be null");
        this.feature = Objects.requireNonNull(feature,
                "feature cannot be null");
        this.state = device.getState(feature);
        this.held = held;
        this.data = data;
    }

}
