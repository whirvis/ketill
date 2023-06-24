package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Container for an extensions parent I/O device.
 * <p>
 * This container exists so only {@link IoDevice} can create new instances
 * of an extension device. This is to prevent invalid device trees from being
 * created.
 *
 * @see IoDevice#addExtension(IoExtension)
 */
public final class ParentIoDevice {

    final @NotNull IoDevice device;

    @IoApi.Friends(IoDevice.class)
    ParentIoDevice(@NotNull IoDevice device) {
        this.device = device;
    }

}