package com.whirvis.ketill;

import java.util.function.Supplier;

class MockDeviceFeature extends DeviceFeature<Object> {

    MockDeviceFeature(String id, Supplier<? super Object> initialState) {
        super(id, initialState);
    }

    MockDeviceFeature(String id) {
        this(id, Object::new);
    }

    MockDeviceFeature() {
        this("mock");
    }

}
