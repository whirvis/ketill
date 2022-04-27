package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class PlainIoFeatureTest {

    @Test
    void __init__() {
        /*
         * Plain I/O features must have a supplier to create an instance of
         * the state. As such, null suppliers, and null values given by the
         * state supplier are illegal.
         */
        assertThrows(NullPointerException.class,
                () -> new MockPlainIoFeature(null));
        assertThrows(NullPointerException.class,
                () -> new MockPlainIoFeature(() -> null));
    }

    private MockPlainIoFeature feature;

    @BeforeEach
    void setup() {
        this.feature = new MockPlainIoFeature(Object::new);
    }

    @Test
    void getState() {
        /*
         * The purpose of a plain I/O feature is to generate a state with
         * an identical internal and container state. Neither state should
         * be null, and the two should be the same object.
         */
        StatePair<?, ?> pair = feature.getState();
        assertNotNull(pair.internal);
        assertNotNull(pair.container);
        assertSame(pair.internal, pair.container);
    }

}
