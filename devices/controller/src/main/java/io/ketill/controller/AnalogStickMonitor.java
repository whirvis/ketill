package io.ketill.controller;

import io.ketill.Direction;
import io.ketill.IoDevice;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureMonitor;
import io.ketill.pressable.PressableFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnalogStickMonitor
        extends PressableFeatureMonitor<AnalogStick, Stick3f> {

    private final Direction direction;
    private final Button1b directionState;

    /* @formatter:off */
    <I extends IoDevice & PressableFeatureSupport>
            AnalogStickMonitor(@NotNull I device,
                               @NotNull AnalogStick stick,
                               @NotNull Direction direction,
                               @NotNull Button1b directionState,
                               @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        super(device, stick, callbackSupplier);
        this.direction = direction;
        this.directionState = directionState;
    }
    /* @formatter:on */

    @Override
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        switch (event.type) {
            case PRESS:
                directionState.pressed = true;
                break;
            case HOLD:
                directionState.held = true;
                break;
            case RELEASE:
                directionState.pressed = false;
                directionState.held = false;
                break;
        }
    }

    @Override
    protected Direction getEventData() {
        return this.direction;
    }

    @Override
    protected boolean isPressed() {
        return AnalogStick.isPressed(state, direction);
    }

}
