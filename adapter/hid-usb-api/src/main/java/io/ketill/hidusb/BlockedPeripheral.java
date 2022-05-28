package io.ketill.hidusb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A simple descriptor for a peripheral which has been blocked for some
 * reason. This is used by {@link PeripheralSeeker} to identify peripherals
 * that should be ignored and what to do with them when they are detached.
 */
public final class BlockedPeripheral<P> {

    public final @NotNull P peripheral;
    public final @Nullable Throwable cause;
    public final boolean unblockAfterDetach;

    BlockedPeripheral(@NotNull P peripheral, @Nullable Throwable cause,
                      boolean unblockAfterDetach) {
        this.peripheral = peripheral;
        this.cause = cause;
        this.unblockAfterDetach = unblockAfterDetach;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        }
        BlockedPeripheral<?> that = (BlockedPeripheral<?>) obj;
        return this.peripheral == that.peripheral
                && this.cause == that.cause
                && this.unblockAfterDetach == that.unblockAfterDetach;
    }

    @Override
    public int hashCode() {
        return Objects.hash(peripheral, cause, unblockAfterDetach);
    }

    @Override
    public String toString() {
        /* @formatter:off */
        return this.getClass().getSimpleName()             + "{"  +
                "peripheral="         + peripheral         + ", " +
                "cause="              + cause              + ", " +
                "unblockAfterDetach=" + unblockAfterDetach + "}";
        /* @formatter:on */
    }

}
