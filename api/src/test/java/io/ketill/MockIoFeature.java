package io.ketill;

import java.util.function.Supplier;

class MockIoFeature extends IoFeature<Object> {

    MockIoFeature(String id, Supplier<? super Object> initialState) {
        super(id, initialState);
    }

    MockIoFeature(String id) {
        this(id, Object::new);
    }

    MockIoFeature() {
        this("mock");
    }

}
