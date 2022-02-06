package com.whirvis.ketill;

class MockIoDeviceAdapter extends IoDeviceAdapter<MockIoDevice> {

    boolean shouldBeConnected;
    private boolean initialized;
    private boolean connected;

    MockIoDeviceAdapter(MockIoDevice device,
                        MappedFeatureRegistry registry) {
        super(device, registry);
    }

    boolean isInitialized() {
        return this.initialized;
    }

    @Override
    protected void initAdapter() {
        this.initialized = true;
    }

    @Override
    protected void pollDevice() {
        this.connected = this.shouldBeConnected;
    }

    @Override
    protected boolean isDeviceConnected() {
        return this.connected;
    }

}
