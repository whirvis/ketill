package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Emitted by {@link IoDevice} when its {@link IoDeviceAdapter} has been
 * initialized.
 */
public final class AdapterInitializedEvent extends IoDeviceEvent {

    AdapterInitializedEvent(@NotNull IoDevice emitter) {
        super(emitter);
    }

}
