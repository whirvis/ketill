package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import io.ketill.KetillException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.Objects;

/**
 * An I/O feature representing an analog stick on a {@link Controller}.
 *
 * @see ControllerButton
 * @see AnalogTrigger
 * @see #isPressed(Vector3fc, Direction)
 */
public final class AnalogStick extends IoFeature<StickPosZ, StickPos> {

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
        Objects.requireNonNull(pos, "pos cannot be null");
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

    /**
     * @param pos       the analog stick position.
     * @param direction the direction to check for.
     * @return {@code true} if {@code pos} is pointing towards
     * {@code direction}, {@code false} otherwise.
     * @throws NullPointerException if {@code direction} is {@code null}.
     */
    public static boolean isPressed(@NotNull StickPos pos,
                                    @NotNull Direction direction) {
        Objects.requireNonNull(direction, "pos cannot be null");
        return isPressed(pos.getPos(true), direction);
    }

    private final @Nullable ControllerButton zButton;
    private final @Nullable AnalogStickCalibration baseCalibration;

    /**
     * @param id              the analog stick ID.
     * @param zButton         the button that, when pressed, should result in
     *                        the Z-axis of this analog stick being decreased
     *                        from {@code 0.0F} to {@code -1.0F}. A value of
     *                        {@code null} is permitted, and indicates that
     *                        there is no button corresponding to the Z-axis.
     * @param baseCalibration the calibration to use when creating a state
     *                        for this analog stick. Note that the state can
     *                        use a different calibration after creation.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogStick(@NotNull String id, @Nullable ControllerButton zButton
            , @Nullable AnalogStickCalibration baseCalibration) {
        super(Controller.class, id);
        this.zButton = zButton;
        this.baseCalibration = baseCalibration;
    }

    /**
     * Constructs a new {@code AnalogStick} with no base calibration. Note
     * that a state can use a different calibration after being created.
     *
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
    public AnalogStick(@NotNull String id, @Nullable ControllerButton zButton) {
        this(id, zButton, null);
    }

    /**
     * Constructs a new {@code AnalogStick} with no Z-button.
     *
     * @param id              the analog stick ID.
     * @param baseCalibration the calibration to use when creating a state
     *                        for this analog stick. Note that the state can
     *                        use a different calibration after creation.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogStick(@NotNull String id,
                       @Nullable AnalogStickCalibration baseCalibration) {
        this(id, null, baseCalibration);
    }

    /**
     * Constructs a new {@code AnalogStick} with no Z-button or base
     * calibration. Note that a state can use a different calibration
     * after being created.
     *
     * @param id the analog stick ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public AnalogStick(@NotNull String id) {
        this(id, null, null);
    }

    /**
     * @return the button that, when pressed, should have the Z-axis of this
     * analog stick decreased from {@code 0.0F} to {@code -1.0F}.
     */
    public @Nullable ControllerButton getZButton() {
        return this.zButton;
    }

    /**
     * @return the calibration to use when creating a state for this
     * analog stick. Note that the state can use a different calibration
     * after creation.
     * @see StickPos#useCalibration(AnalogStickCalibration)
     */
    public @Nullable AnalogStickCalibration getBaseCalibration() {
        return this.baseCalibration;
    }

    @Override
    protected @NotNull StickPosZ getInternalState(@NotNull IoDeviceObserver observer) {
        return new StickPosZ(this, observer, baseCalibration);
    }

    @Override
    protected @NotNull StickPos getContainerState(@NotNull StickPosZ internalState) {
        return new StickPos(internalState);
    }

}
