package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class PlainIoFeatureTest {

    private MockPlainIoFeature feature;

    @BeforeEach
    void createFeature() {
        this.feature = new MockPlainIoFeature(Object::new);
    }

    @Test
    void testInit() {
        /*
         * Plain I/O features must have a supplier to create an instance
         * of the state. As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockPlainIoFeature(null));
    }

    @Test
    void testGetState() {
        IoDeviceObserver observer = mock(IoDeviceObserver.class);

        /*
         * The purpose of a plain I/O feature is to generate a state with
         * an identical internal and container state. Neither state should
         * be null, and the two should be the same object.
         */
        StatePair<?, ?> pair = feature.getState(observer);
        assertNotNull(pair.internal);
        assertNotNull(pair.container);
        assertSame(pair.internal, pair.container);
    }

}
