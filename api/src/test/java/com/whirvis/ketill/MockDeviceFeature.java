package com.whirvis.ketill;

class MockDeviceFeature extends DeviceFeature<Object> {

    MockDeviceFeature(String id) {
        super(id, Object::new);
    }

}
