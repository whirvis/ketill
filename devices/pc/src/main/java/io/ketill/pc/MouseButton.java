package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

/**
 * An I/O feature representing a button on a {@link Mouse}.
 */
public final class MouseButton extends IoFeature<MouseClickZ, MouseClick> {

    /**
     * Constructs a new {@code MouseButton}.
     *
     * @param id the mouse button ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public MouseButton(@NotNull String id) {
        super(Mouse.class, id);
    }

    @Override
    protected @NotNull MouseClickZ getInternalState(@NotNull IoDeviceObserver observer) {
        return new MouseClickZ(this, observer);
    }

    @Override
    protected @NotNull MouseClick getContainerState(@NotNull MouseClickZ internalState) {
        return new MouseClick(internalState);
    }

}
