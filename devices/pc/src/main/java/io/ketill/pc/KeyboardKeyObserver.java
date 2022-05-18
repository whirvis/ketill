package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import io.ketill.pressable.PressableIoFeatureObserver;
import org.jetbrains.annotations.NotNull;

final class KeyboardKeyObserver extends PressableIoFeatureObserver<KeyPressZ> {

    private final KeyboardKey key;

    KeyboardKeyObserver(@NotNull KeyboardKey key,
                        @NotNull KeyPressZ internalState,
                        @NotNull IoDeviceObserver observer) {
        super(key, internalState, observer);
        this.key = key;
    }

    @Override
    protected boolean isPressed() {
        return internalState.pressed;
    }

    @Override
    protected void onPress() {
        this.onNext(new KeyboardKeyPressEvent(device, key));
    }

    @Override
    protected void onHold() {
        internalState.held = true;
        this.onNext(new KeyboardKeyHoldEvent(device, key));
    }

    @Override
    protected void onRelease() {
        internalState.held = false;
        this.onNext(new KeyboardKeyReleaseEvent(device, key));
    }

}
