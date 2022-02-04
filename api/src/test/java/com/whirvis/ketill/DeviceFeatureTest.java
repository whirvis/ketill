package com.whirvis.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceFeatureTest {

    @Test /* use @BeforeAll if more tests are added */
    void __init__() {
        /*
         * The feature must be identifiable and have a supplier to create an
         * instance of the initial state. As such, null IDs, null suppliers,
         * and null values given by the state supplier are illegal.
         */
        assertThrows(NullPointerException.class,
                () -> new MockDeviceFeature(null, Object::new));
        assertThrows(NullPointerException.class,
                () -> new MockDeviceFeature("mock", null));
        assertThrows(NullPointerException.class,
                () -> new MockDeviceFeature("mock", () -> null));

        /*
         * It makes no sense for the device feature's ID to be blank.
         * Furthermore, any whitespace in an ID is illegal.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockDeviceFeature("", Object::new));
        assertThrows(IllegalArgumentException.class,
                () -> new MockDeviceFeature("\t", Object::new));
    }

}