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
    public final Object featureState;

    MockIoDevice(String id,
                 AdapterSupplier<MockIoDevice> adapterSupplier,
                 boolean registerFields, boolean initAdapter) {
        super(id, adapterSupplier, registerFields, initAdapter);
        if (registerFields) {
            this.featureState = this.getState(FEATURE);
        } else {
            this.featureState = null;
        }
    }

    MockIoDevice(@NotNull String id,
                 @NotNull AdapterSupplier<MockIoDevice> adapterSupplier) {
        super(id, adapterSupplier);
        this.featureState = this.getState(FEATURE);
    }

    MockIoDevice() {
        this("mock", MockIoDeviceAdapter::new);
    }

}
