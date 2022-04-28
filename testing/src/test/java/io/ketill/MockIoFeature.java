package io.ketill;

class MockIoFeature extends PlainIoFeature<Object> {

    MockIoFeature() {
        super("mock", Object::new);
    }

}
