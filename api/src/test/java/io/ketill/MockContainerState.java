package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockContainerState extends ContainerState<Object> {

    MockContainerState(@NotNull Object internalState) {
        super(internalState);
    }

    MockContainerState() {
        super(new Object());
    }

}
