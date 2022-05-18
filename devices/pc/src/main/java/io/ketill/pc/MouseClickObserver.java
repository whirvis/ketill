package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class MouseClickObserver extends PressableIoFeatureObserver<MouseClickZ> {

    private final MouseButton button;

    MouseClickObserver(@NotNull MouseButton button,
                       @NotNull MouseClickZ internalState,
                       @NotNull IoDeviceObserver observer) {
        super(button, internalState, observer);
        this.button = button;
    }

    @Override
    protected boolean isPressed() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new MouseButtonPressEvent(device, button));
    }

    @Override
    protected void onHold() {
        internalState.held = true;
        this.onNext(new MouseButtonHoldEvent(device, button));
    }

    @Override
    protected void onRelease() {
        internalState.held = false;
        this.onNext(new MouseButtonReleaseEvent(device, button));
    }

}
