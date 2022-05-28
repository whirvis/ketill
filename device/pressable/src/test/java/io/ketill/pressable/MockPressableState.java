package io.ketill.pressable;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

class MockPressableState implements PressableState {

    boolean pressed;
    boolean held;

    final IoFeature<?, ?> feature;
    final MockIoFeatureObserver observer;

    MockPressableState(@NotNull MockIoFeature feature,
                       @NotNull IoDeviceObserver observer) {
        this.feature = feature;
        this.observer = new MockIoFeatureObserver(feature, this, observer);
    }

    @Override
    public boolean isPressed() {
        return this.pressed;
    }

    @Override
    public boolean isHeld() {
        return this.held;
    }

}
