package io.ketill.controller;

import io.ketill.Direction;
import io.ketill.IoFeature;
import io.ketill.KetillException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Objects;

/**
 * An I/O feature representing an analog stick on a {@link Controller}.
 *
 * @see DeviceButton
 * @see AnalogTrigger
 * @see #isPressed(Vector3fc, Direction)
 */
public class AnalogStick extends IoFeature<StickPosZ, StickPos> {

    private static final float STICK_PRESS = 2.0F / 3.0F;

    /**
     * @param pos       the analog stick position.
     * @param direction the direction to check for.
     * @return {@code true} if {@code pos} is pointing towards
     * {@code direction}, {@code false} otherwise.
     * @throws NullPointerException if {@code direction} is {@code null}.
     */
    public static boolean isPressed(@NotNull Vector3fc pos,
                                    @NotNull Direction direction) {
        Objects.requireNonNull(direction, "direction cannot be null");
        switch (direction) {
            case UP:
                return pos.y() >= STICK_PRESS;
            case DOWN:
                return pos.y() <= -STICK_PRESS;
            case LEFT:
                return pos.x() <= -STICK_PRESS;
            case RIGHT:
                return pos.x() >= STICK_PRESS;
        }
        throw new KetillException("this is a bug");
    }

    public final @Nullable DeviceButton zButton;

    /**
     * @param id      the analog stick ID.
     * @param zButton the button that, when pressed, should have the Z-axis
     *                of this analog stick decreased from {@code 0.0F} to
     *                {@code -1.0F}. A value of {@code null} is permitted,
     *                and indicates that no button corresponds to the Z-axis
     *                of this analog stick.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogStick(@NotNull String id, @Nullable DeviceButton zButton) {
        super(id);
        this.zButton = zButton;
    }

    /**
     * Constructs a new {@code AnalogStick} with no Z-button.
     *
     * @param id the analog stick ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogStick(@NotNull String id) {
        this(id, null);
    }

    @Override
    protected @NotNull StickPosZ getInternalState() {
        return new StickPosZ();
    }

    @Override
    protected @NotNull StickPos getContainerState(@NotNull StickPosZ internalState) {
        return new StickPos(internalState);
    }

}
