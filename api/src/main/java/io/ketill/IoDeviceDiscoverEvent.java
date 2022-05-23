package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Emitted by {@link IoDeviceSeeker} when it discovers an I/O device.
 *
 * @see IoDeviceSeeker#getDeviceCount()
 * @see IoDeviceSeeker#forEachDevice(Consumer)
 */
public final class IoDeviceDiscoverEvent extends IoDeviceSeekerEvent {

    private final @NotNull IoDevice device;

    /**
     * @param seeker the seeker which emitted this event.
     * @param device the device which was discovered.
     * @throws NullPointerException if {@code seeker} or {@code device}
     *                              are {@code null}.
     */
    IoDeviceDiscoverEvent(@NotNull IoDeviceSeeker<?> seeker,
                          @NotNull IoDevice device) {
        super(seeker);
        this.device = Objects.requireNonNull(device, "device cannot be null");
    }

    /**
     * @return the device which was discovered.
     */
    public @NotNull IoDevice getDevice() {
        return this.device;
    }

}
