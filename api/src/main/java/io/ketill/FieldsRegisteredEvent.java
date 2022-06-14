package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when it has registered all fields
 * present in its class annotated with {@link FeaturePresent}.
 */
public final class FieldsRegisteredEvent extends IoDeviceEvent {

    FieldsRegisteredEvent(@NotNull IoDevice emitter) {
        super(emitter);
    }

}
