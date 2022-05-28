package io.ketill.pressable;

import io.ketill.IoDeviceObserver;
import io.ketill.IoFeature;
import org.jetbrains.annotations.NotNull;

class MockIoFeature extends IoFeature<MockPressableState, MockPressableState> {

    MockIoFeature() {
        super("mock");
    }

    @Override
    protected @NotNull MockPressableState getInternalState(@NotNull IoDeviceObserver observer) {
        return new MockPressableState(this, observer);
    }

    @Override
    protected @NotNull MockPressableState getContainerState(@NotNull MockPressableState internalState) {
        return internalState;
    }
}
