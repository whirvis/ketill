package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class ControllerButtonObserver
        extends PressableIoFeatureObserver<ButtonStateZ> {

    private final ControllerButton button;
    private final Controller controller;

    ControllerButtonObserver(@NotNull ControllerButton button,
                             @NotNull ButtonStateZ internalState,
                             @NotNull IoDeviceObserver observer) {
        super(button, internalState, observer);
        this.button = button;
        this.controller = (Controller) observer.getDevice();
    }

    @Override
    protected boolean isPressedImpl() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new ControllerButtonPressEvent(controller, button));
    }

    @Override
    protected void onHold() {
        this.onNext(new ControllerButtonHoldEvent(controller, button));
    }

    @Override
    protected void onRelease() {
        this.onNext(new ControllerButtonReleaseEvent(controller, button));
    }

    @Override
    public void poll() {
        super.poll();
        internalState.held = this.isHeld();
    }

}
