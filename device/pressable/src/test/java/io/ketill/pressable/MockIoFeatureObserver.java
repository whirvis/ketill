package io.ketill.pressable;

import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;

class MockIoFeatureObserver extends PressableIoFeatureObserver<MockPressableState> {

    MockIoFeatureObserver(@NotNull MockIoFeature feature,
                          @NotNull MockPressableState internalState,
                          @NotNull IoDeviceObserver observer) {
        super(feature, internalState, observer);
    }

    @Override
    public boolean isPressedImpl() {
        return internalState.pressed;
    }

}
