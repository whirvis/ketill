package io.ketill.pressable;

import io.ketill.IoDevice;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;

class MockIoDeviceAdapter extends IoDeviceAdapter<MockIoDevice> {

    MockIoDeviceAdapter(MockIoDevice device, MappedFeatureRegistry registry) {
        super(device, registry);
    }

    @Override
    protected void initAdapter() {
        /* nothing to do */
    }

    @Override
    protected void pollDevice() {
        /* nothing to do */
    }

    @Override
    protected boolean isDeviceConnected() {
        return false;
    }

}
