package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import io.ketill.KetillException;
import io.ketill.ToStringUtils;
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
     * Returns if a given position indicates an analog stick is pressed
     * towards a certain direction. An analog stick is considered to be
     * pressed if the corresponding axis has an absolute value greater
     * than or equal to {@code 2.0F / 3.0F}.
     *
     * @param pos       the analog stick position.
     * @param direction the direction to check for.
     * @return {@code true} if {@code pos} is pointing towards
     * {@code direction}, {@code false} otherwise.
     * @throws NullPointerException if {@code pos} or {@code direction}
     *                              are {@code null}.
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
            default:
                throw new KetillException("this is a bug");
        }
    }

    /**
     * Returns if a given position indicates an analog stick is pressed
     * towards a certain direction. An analog stick is considered to be
     * pressed if the corresponding axis has an absolute value greater
     * than or equal to {@code 2.0F / 3.0F}.
     * <p>
     * <b>Shorthand for:</b> {@link #isPressed(Vector3fc, Direction)}, with
     * the argument for {@code pos} being {@code state.getPos(true)}.
     *
     * @param state     the analog stick position.
     * @param direction the direction to check for.
     * @return {@code true} if {@code state} is pointing towards
     * {@code direction}, {@code false} otherwise.
     * @throws NullPointerException if {@code state} or {@code direction}
     *                              are {@code null}.
     */
    public static boolean isPressed(@NotNull StickPos state,
                                    @NotNull Direction direction) {
        Objects.requireNonNull(direction, "state cannot be null");
        return isPressed(state.getPos(true), direction);
    }

    private final @Nullable ControllerButton zButton;
    private final @Nullable AnalogStickCalibration baseCalibration;

    /**
     * Constructs a new {@code AnalogStick}.
     *
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
     * Returns the thumb button for this analog stick.
     *
     * @return the button that, when pressed, should have the Z-axis of
     * this analog stick decreased from {@code 0.0F} to {@code -1.0F}.
     */
    public @Nullable ControllerButton getZButton() {
        return this.zButton;
    }

    /**
     * Returns the base calibration.
     * <p>
     * This is the calibration that will be used when getting the state
     * for this analog stick. However, this is only the initial value.
     * After creation, {@link StickPos} can use a different calibration.
     *
     * @return the calibration that will be used when getting the state
     * for this analog stick.
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

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(super.toString(), this)
                .add("zButton=" + zButton)
                .add("baseCalibration=" + baseCalibration)
                .toString();
    }
    /* @formatter:on */

}
