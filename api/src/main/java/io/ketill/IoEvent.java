package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * The base for events relating to I/O.
 * <p>
 * This class exists so events not extending from {@link IoDeviceEvent} can
 * still be listened for.
 */
public interface IoEvent {

    /**
     * @return the I/O device which emitted this event.
     */
    @NotNull IoDevice getDevice();

}
