package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Emitted by {@link IoDeviceSeeker} when it discovers an I/O device.
 *
 * @see IoDeviceSeeker#getDeviceCount()
 * @see IoDeviceSeeker#forEachDevice(Consumer)
 */
public final class IoDeviceDiscoverEvent extends IoDeviceSeekerEvent {

    private final @NotNull IoDevice device;

    IoDeviceDiscoverEvent(@NotNull IoDeviceSeeker<?> seeker,
                          @NotNull IoDevice device) {
        super(seeker);
        this.device = device;
    }

    /**
     * Returns the device which was discovered.
     *
     * @return the device which was discovered.
     */
    public @NotNull IoDevice getDevice() {
        return this.device;
    }

}
