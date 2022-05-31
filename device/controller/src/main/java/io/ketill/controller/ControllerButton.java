package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import io.ketill.ToStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An I/O feature representing a button on a {@link Controller}.
 *
 * @see AnalogStick
 * @see AnalogTrigger
 */
public final class ControllerButton
        extends IoFeature<ButtonStateZ, ButtonState> {

    private final @Nullable Direction direction;

    /**
     * Constructs a new {@code ControllerButton}.
     *
     * @param id        the button ID.
     * @param direction the direction this button represents. A {@code null}
     *                  value is permitted, and indicates that this button
     *                  does not represent a direction.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public ControllerButton(@NotNull String id, @Nullable Direction direction) {
        super(Controller.class, id);
        this.direction = direction;
    }

    /**
     * Constructs a new {@code ControllerButton}.
     *
     * @param id the button ID.
     * @throws NullPointerException     if {@code id} is {@code null}.
     * @throws IllegalArgumentException if {@code id} is empty or contains
     *                                  whitespace.
     */
    public ControllerButton(@NotNull String id) {
        this(id, null);
    }

    /**
     * @return the direction this button represents, {@code null} if this
     * button does not represent a  direction.
     */
    public @Nullable Direction getDirection() {
        return this.direction;
    }

    @Override
    protected @NotNull ButtonStateZ getInternalState(@NotNull IoDeviceObserver observer) {
        return new ButtonStateZ(this, observer);
    }

    @Override
    protected @NotNull ButtonState getContainerState(@NotNull ButtonStateZ internalState) {
        return new ButtonState(internalState);
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(super.toString(), this)
                .add("direction=" + direction)
                .toString();
    }
    /* @formatter:on */

}
