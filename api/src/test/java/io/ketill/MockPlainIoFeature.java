package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

class MockPlainIoFeature extends PlainIoFeature<Object> {

    MockPlainIoFeature(@NotNull Supplier<? super Object> initialState) {
        super("mock", initialState);
    }

}
