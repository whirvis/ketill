package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoFeatureTest {

    private MockIoFeature feature;

    @BeforeEach
    void createFeature() {
        this.feature = new MockIoFeature();
    }

    @Test
    void testInit() {
        /*
         * It makes no sense for the feature's ID to be null or blank.
         * Furthermore, any whitespace in an ID is illegal.
         */
        assertThrows(NullPointerException.class,
                () -> new MockIoFeature(null));
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoFeature(""));
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoFeature("\t"));
    }

    @Test
    void testGetState() {
        assertThrows(NullPointerException.class,
                () -> feature.getState(null));

        IoDeviceObserver observer = mock(IoDeviceObserver.class);

        /*
         * It would not make sense for the internal state of an I/O
         * feature to be null. As such, assume this was a mistake by
         * the user and throw an exception.
         */
        feature.internalState = null;
        assertThrows(NullPointerException.class,
                () -> feature.getState(observer));

        /*
         * The internal state of an I/O feature is not allowed to be
         * another I/O feature. This is to prevent possible headaches
         * and confusion that would result from doing such a thing.
         */
        feature.internalState = new MockIoFeature();
        assertThrows(KetillException.class,
                () -> feature.getState(observer));

        /*
         * The container state of an I/O feature is not allowed to be
         * a container. This is to enforce a proper hierarchy of state
         * classes for an I/O feature.
         */
        feature.internalState = new MockContainerState();
        assertThrows(KetillException.class,
                () -> feature.getState(observer));

        /* use valid internal state for next test */
        feature.internalState = new Object();

        /*
         * It would not make sense for the container state of an I/O
         * feature to be null. As such, assume this was a mistake by
         * the user and throw an exception.
         */
        feature.containerState = null;
        assertThrows(NullPointerException.class,
                () -> feature.getState(observer));

        /*
         * The container state of an I/O feature is not allowed to be
         * another I/O feature. This is to prevent possible headaches
         * and confusion that would result from doing such a thing.
         */
        feature.containerState = new MockIoFeature();
        assertThrows(KetillException.class,
                () -> feature.getState(observer));

        /*
         * The container state of an I/O feature is not allowed to be
         * autonomous. This is to enforce a proper hierarchy of state
         * classes for an I/O feature.
         */
        feature.containerState = new MockAutonomousState();
        assertThrows(KetillException.class,
                () -> feature.getState(observer));

        /* use valid container state for next test */
        feature.containerState = new Object();

        /*
         * Once valid states are returned by the I/O feature, the state
         * container returned should contain the states provided by the
         * extending class.
         */
        StatePair<?, ?> pair = feature.getState(observer);
        assertNotNull(pair);
        assertSame(pair.internal, feature.internalState);
        assertSame(pair.container, feature.containerState);
    }

}