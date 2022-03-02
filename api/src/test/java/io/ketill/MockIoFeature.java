package io.ketill;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

class MockIoFeature extends IoFeature<Object> {

    MockIoFeature(@NotNull String id,
                  @NotNull Supplier<? super Object> initialState) {
        super(id, initialState);
    }

    MockIoFeature(@NotNull String id) {
        this(id, Object::new);
    }

    MockIoFeature() {
        this("mock");
    }

}
