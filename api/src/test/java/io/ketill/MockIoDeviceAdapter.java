package io.ketill;

class MockIoDeviceAdapter extends IoDeviceAdapter<MockIoDevice> {

    boolean shouldBeConnected;
    boolean errorOnPoll;
    private boolean initialized;
    private boolean connected;

    MockIoDeviceAdapter(MockIoDevice device, MappedFeatureRegistry registry) {
        super(device, registry);
    }

    boolean isInitialized() {
        return this.initialized;
    }

    @Override
    protected void initAdapter() {
        registry.mapFeature(MockIoDevice.FEATURE, (state, feature) -> {
            /* nothing to update */
        });
        this.initialized = true;
    }

    @Override
    protected void pollDevice() {
        if (errorOnPoll) {
            throw new RuntimeException();
        }
        this.connected = this.shouldBeConnected;
    }

    @Override
    protected boolean isDeviceConnected() {
        return this.connected;
    }

}
