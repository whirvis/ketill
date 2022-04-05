package io.ketill.pressable;

import io.ketill.IoFeature;

class MockIoFeature extends IoFeature<Object> {

    MockIoFeature() {
        super("mock", Object::new);
    }

}
