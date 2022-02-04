package com.whirvis.ketill;

class MockInputDevice extends InputDevice {

    @SuppressWarnings("unused")
    static class WithPrivateFeature extends MockInputDevice {

        @FeaturePresent
        private static final MockDeviceFeature
                FEATURE = new MockDeviceFeature("feature");

    }

    @SuppressWarnings("unused")
    static class WithUnassignableFeature extends MockInputDevice {

        @FeaturePresent
        public static final String FEATURE = "feature";

    }

    @FeaturePresent
    public static final DeviceFeature<String>
            FEATURE = new DeviceFeature<>("feature", String::new);

    MockInputDevice(String id,
                    AdapterSupplier<MockInputDevice> adapterSupplier,
                    boolean registerFields, boolean initAdapter) {
        super(id, adapterSupplier, registerFields, initAdapter);
    }

    MockInputDevice(String id,
                    AdapterSupplier<MockInputDevice> adapterSupplier) {
        super(id, adapterSupplier);
    }

    private MockInputDevice() {
        this("mock", MockDeviceAdapter::new);
    }

}
