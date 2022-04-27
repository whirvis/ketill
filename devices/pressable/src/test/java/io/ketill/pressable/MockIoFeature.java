package io.ketill.pressable;

import io.ketill.PlainIoFeature;

class MockIoFeature extends PlainIoFeature<Object> {

    MockIoFeature() {
        super("mock", Object::new);
    }

}
