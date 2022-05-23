package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class KeyboardKeyObserver extends PressableIoFeatureObserver<KeyPressZ> {

    private final Keyboard keyboard;
    private final KeyboardKey key;

    KeyboardKeyObserver(@NotNull KeyboardKey key,
                        @NotNull KeyPressZ internalState,
                        @NotNull IoDeviceObserver observer) {
        super(key, internalState, observer);
        this.keyboard = (Keyboard) observer.getDevice();
        this.key = key;
    }

    @Override
    protected boolean isPressedImpl() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new KeyboardKeyPressEvent(keyboard, key));
    }

    @Override
    protected void onHold() {
        this.onNext(new KeyboardKeyHoldEvent(keyboard, key));
    }

    @Override
    protected void onRelease() {
        this.onNext(new KeyboardKeyReleaseEvent(keyboard, key));
    }

    @Override
    public void poll() {
        super.poll();
        internalState.held = this.isHeld();
    }

}
