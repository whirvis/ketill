package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IoFeatureTest {

    @Test /* use @BeforeAll if more tests are added */
    void __init__() {
        /*
         * The feature must be identifiable and have a supplier to create an
         * instance of the initial state. As such, null IDs, null suppliers,
         * and null values given by the state supplier are illegal.
         */
        assertThrows(NullPointerException.class,
                () -> new MockIoFeature(null, Object::new));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeature("mock", null));
        assertThrows(NullPointerException.class,
                () -> new MockIoFeature("mock", () -> null));

        /*
         * It makes no sense for the feature's ID to be blank.
         * Furthermore, any whitespace in an ID is illegal.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoFeature("", Object::new));
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoFeature("\t", Object::new));
    }

}