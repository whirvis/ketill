package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class DeviceButtonObserver extends PressableIoFeatureObserver<ButtonStateZ> {

    private final @NotNull DeviceButton button;

    DeviceButtonObserver(@NotNull DeviceButton button,
                         @NotNull ButtonStateZ internalState,
                         @NotNull IoDeviceObserver observer) {
        super(button, internalState, observer);
        this.button = button; /* prevent casting */
    }

    @Override
    protected boolean isPressed() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new DeviceButtonPressEvent(device, button));
    }

    @Override
    protected void onHold() {
        internalState.held = true;
        this.onNext(new DeviceButtonHoldEvent(device, button));
    }

    @Override
    protected void onRelease() {
        internalState.held = false;
        this.onNext(new DeviceButtonReleaseEvent(device, button));
    }

}
