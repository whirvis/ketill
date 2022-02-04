package com.whirvis.ketill;

class MockDeviceAdapter extends DeviceAdapter<MockInputDevice> {

    final MappedFeatureRegistry registry;
    boolean shouldBeConnected;
    private boolean initialized;
    private boolean connected;

    MockDeviceAdapter(MockInputDevice device,
                             MappedFeatureRegistry registry) {
        super(device, registry);
        this.registry = registry;
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
