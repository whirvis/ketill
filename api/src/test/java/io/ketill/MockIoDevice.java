package io.ketill;

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

    MockIoDevice(String id,
                 AdapterSupplier<MockIoDevice> adapterSupplier,
                 boolean registerFields, boolean initAdapter) {
        super(id, adapterSupplier, registerFields, initAdapter);
    }

    MockIoDevice(String id,
                 AdapterSupplier<MockIoDevice> adapterSupplier) {
        super(id, adapterSupplier);
    }

    MockIoDevice() {
        this("mock", MockIoDeviceAdapter::new);
    }

}
