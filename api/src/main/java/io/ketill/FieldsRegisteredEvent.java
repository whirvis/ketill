package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when it has registered all fields
 * present in its class annotated with {@link FeaturePresent}.
 */
public class FieldsRegisteredEvent extends IoDeviceEvent {

    /**
     * Constructs a new {@code FieldsRegisteredEvent}.
     *
     * @param device the device which emitted this event.
     * @throws NullPointerException if {@code device} is {@code null}.
     */
    public FieldsRegisteredEvent(@NotNull IoDevice device) {
        super(device);
    }

}
