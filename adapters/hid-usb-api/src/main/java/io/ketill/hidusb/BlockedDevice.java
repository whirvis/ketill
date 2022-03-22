package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a device that has been blocked by a {@link SystemDeviceSeeker}.
 * Inside is the blocked device, why it was blocked (if specified), and if it
 * will be unblocked after the device is detached.
 *
 * @param <S> the system device type.
 */
public final class BlockedDevice<S> {

    public final @NotNull S systemDevice;
    public final @Nullable Throwable cause;
    public final boolean unblockAfterDetach;

    BlockedDevice(@NotNull S systemDevice, @Nullable Throwable cause,
                  boolean unblockAfterDetach) {
        this.systemDevice = systemDevice;
        this.cause = cause;
        this.unblockAfterDetach = unblockAfterDetach;
    }

    /* @formatter:off */
    @Override /* generated by IntelliJ IDEA */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockedDevice<?> that = (BlockedDevice<?>) o;
        return systemDevice.equals(that.systemDevice);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override /* generated by IntelliJ IDEA */
    public int hashCode() {
        return Objects.hash(systemDevice);
    }
    /* @formatter:on */

    /* @formatter:off */
    @Override /* generated by IntelliJ IDEA */
    public String toString() {
        return "BlockedDevice{" +
                "systemDevice=" + systemDevice +
                ", cause=" + cause +
                ", unblockAfterDetach=" + unblockAfterDetach +
                '}';
    }
    /* @formatter:on */

}