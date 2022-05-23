package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class ControllerButtonObserver
        extends PressableIoFeatureObserver<ButtonStateZ> {

    private final ControllerButton button;

    ControllerButtonObserver(@NotNull ControllerButton button,
                             @NotNull ButtonStateZ internalState,
                             @NotNull IoDeviceObserver observer) {
        super(button, internalState, observer);
        this.button = button;
    }

    @Override
    protected boolean isPressedImpl() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new ControllerButtonPressEvent(device, button));
    }

    @Override
    protected void onHold() {
        this.onNext(new ControllerButtonHoldEvent(device, button));
    }

    @Override
    protected void onRelease() {
        this.onNext(new ControllerButtonReleaseEvent(device, button));
    }

    @Override
    public void poll() {
        super.poll();
        internalState.held = this.isHeld();
    }

}
