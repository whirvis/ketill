package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class MouseClickObserver extends PressableIoFeatureObserver<MouseClickZ> {

    private final Mouse mouse;
    private final MouseButton button;

    MouseClickObserver(@NotNull MouseButton button,
                       @NotNull MouseClickZ internalState,
                       @NotNull IoDeviceObserver observer) {
        super(button, internalState, observer);
        this.mouse = (Mouse) observer.getDevice();
        this.button = button;
    }

    @Override
    protected boolean isPressedImpl() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new MouseButtonPressEvent(mouse, button));
    }

    @Override
    protected void onHold() {
        this.onNext(new MouseButtonHoldEvent(mouse, button));
    }

    @Override
    protected void onRelease() {
        this.onNext(new MouseButtonReleaseEvent(mouse, button));
    }

    @Override
    public void poll() {
        super.poll();
        internalState.held = this.isHeld();
    }

}
