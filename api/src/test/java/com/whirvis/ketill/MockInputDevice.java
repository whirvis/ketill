package com.whirvis.ketill;

class MockInputDevice extends InputDevice {

    static class WithPrivateFeature extends MockInputDevice {

        @FeaturePresent
        @SuppressWarnings("unused")
        private static final MockDeviceFeature
                FEATURE = new MockDeviceFeature();

    }

    static class WithUnassignableFeature extends MockInputDevice {

        @FeaturePresent
        @SuppressWarnings("unused")
        public static final String
                FEATURE = "feature";

    }

    @FeaturePresent
    public static final MockDeviceFeature
            FEATURE = new MockDeviceFeature();

    MockInputDevice(String id,
                    AdapterSupplier<MockInputDevice> adapterSupplier,
                    boolean registerFields, boolean initAdapter) {
        super(id, adapterSupplier, registerFields, initAdapter);
    }

    MockInputDevice(String id,
                    AdapterSupplier<MockInputDevice> adapterSupplier) {
        super(id, adapterSupplier);
    }

    MockInputDevice() {
        this("mock", MockDeviceAdapter::new);
    }

}
