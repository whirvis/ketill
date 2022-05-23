package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class AnalogStickObserver extends PressableIoFeatureObserver<StickPosZ> {

    private final AnalogStick stick;
    private final Direction direction;
    private final ButtonStateZ buttonState;

    AnalogStickObserver(@NotNull AnalogStick stick,
                        @NotNull Direction direction,
                        @NotNull StickPosZ internalState,
                        @NotNull ButtonStateZ buttonState,
                        @NotNull IoDeviceObserver observer) {
        super(stick, internalState, observer);
        this.stick = stick;
        this.direction = direction;
        this.buttonState = buttonState;
    }

    @Override
    protected boolean isPressedImpl() {
        return AnalogStick.isPressed(internalState.calibratedPos, direction);
    }

    @Override
    protected void onPress() {
        this.onNext(new AnalogStickPressEvent(device, stick, direction));
    }

    @Override
    protected void onHold() {
        this.onNext(new AnalogStickHoldEvent(device, stick, direction));
    }

    @Override
    protected void onRelease() {
        this.onNext(new AnalogStickReleaseEvent(device, stick, direction));
    }

    @Override
    public void poll() {
        super.poll();
        buttonState.pressed = this.isPressed();
        buttonState.held = this.isHeld();
    }

}
