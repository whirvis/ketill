package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockStateContainer extends StateContainer<Object> {

    MockStateContainer(@NotNull Object internalState) {
        super(internalState);
    }

}
