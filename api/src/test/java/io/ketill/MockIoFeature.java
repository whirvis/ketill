package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoFeature extends IoFeature<Object, Object> {

    Object internalState;
    Object containerState;

    MockIoFeature(@NotNull Class<? extends IoDevice> requiredType,
                  @NotNull String id) {
        super(requiredType, id);
        this.internalState = new Object();
        this.containerState = new Object();
    }

    MockIoFeature(@NotNull Class<? extends IoDevice> requiredType) {
        this(requiredType, "mock");
    }

    MockIoFeature() {
        super("mock"); /* use super for coverage */
        this.internalState = new Object();
        this.containerState = new Object();
    }

    @Override
    protected @NotNull Object getInternalState(@NotNull IoDeviceObserver observer) {
        return this.internalState;
    }

    @Override
    protected @NotNull Object getContainerState(@NotNull Object internalState) {
        return this.containerState;
    }

}
