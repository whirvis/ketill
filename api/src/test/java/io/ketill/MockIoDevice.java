package io.ketill;

import org.jetbrains.annotations.NotNull;

class MockIoDevice extends IoDevice {

    static class WithPrivateFeature extends MockIoDevice {

        @FeaturePresent
        @SuppressWarnings("unused")
        private static final MockIoFeature
                FEATURE = new MockIoFeature();

    }

    static class WithUnassignableFeature extends MockIoDevice {

        @FeaturePresent
        @SuppressWarnings("unused")
        public static final String
                FEATURE = "feature";

    }

    @FeaturePresent
    public static final MockIoFeature
            FEATURE = new MockIoFeature();

    @FeatureState
    public final Object featureContainerState, featureInternalState;

    boolean executedTask;
    boolean featureRegistered, featureUnregistered;
    boolean deviceConnected, deviceDisconnected;
    boolean polled;

    MockIoDevice(String typeId,
                 AdapterSupplier<MockIoDevice> adapterSupplier,
                 boolean registerFields, boolean initAdapter) {
        super(typeId, adapterSupplier, registerFields, initAdapter);
        if (registerFields) {
            this.featureContainerState = this.getState(FEATURE);
            this.featureInternalState = this.getInternalState(FEATURE);
        } else {
            this.featureContainerState = null;
            this.featureInternalState = null;
        }
    }

    MockIoDevice(@NotNull String typeId,
                 @NotNull AdapterSupplier<MockIoDevice> adapterSupplier) {
        super(typeId, adapterSupplier);
        this.featureContainerState = this.getState(FEATURE);
        this.featureInternalState = this.getInternalState(FEATURE);
    }

    MockIoDevice() {
        this("mock", MockIoDeviceAdapter::new);
    }

    void executeTask() {
        this.executedTask = true;
    }

    @Override
    protected void featureRegistered(@NotNull RegisteredIoFeature<?, ?, ?> registered) {
        this.featureRegistered = true;
    }

    @Override
    protected void featureUnregistered(@NotNull IoFeature<?, ?> feature) {
        this.featureUnregistered = true;
    }

    @Override
    protected void deviceConnected() {
        this.deviceConnected = true;
    }

    @Override
    protected void deviceDisconnected() {
        this.deviceDisconnected = true;
    }

    @Override
    public void poll() {
        super.poll();
        this.polled = true;
    }

}
