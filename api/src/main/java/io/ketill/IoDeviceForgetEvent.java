package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Emitted by {@link IoDeviceSeeker} when it forgets an I/O device.
 *
 * @see IoDeviceSeeker#getDeviceCount()
 * @see IoDeviceSeeker#forEachDevice(Consumer)
 */
public final class IoDeviceForgetEvent extends IoDeviceSeekerEvent {

    private final @NotNull IoDevice device;

    IoDeviceForgetEvent(@NotNull IoDeviceSeeker<?> seeker,
                        @NotNull IoDevice device) {
        super(seeker);
        this.device = device;
    }

    /**
     * Returns the device which was forgotten.
     *
     * @return the device which was forgotten.
     */
    public @NotNull IoDevice getDevice() {
        return this.device;
    }

}
