package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        /*
         * It would not make sense for the internal state of an I/O
         * feature to be null. As such, assume this was a mistake by
         * the user and throw an exception.
         */
        feature.internalState = null;
        assertThrows(NullPointerException.class,
                () -> feature.getState());

        /*
         * The internal state of an I/O feature is not allowed to be
         * another I/O feature. This is to prevent possible headaches
         * and confusion that would result from doing such a thing.
         */
        feature.internalState = new MockIoFeature();
        assertThrows(UnsupportedOperationException.class,
                () -> feature.getState());

        /* use valid internal state for next test */
        feature.internalState = new Object();

        /*
         * It would not make sense for the container state of an I/O
         * feature to be null. As such, assume this was a mistake by
         * the user and throw an exception.
         */
        feature.containerState = null;
        assertThrows(NullPointerException.class,
                () -> feature.getState());

        /*
         * The container state of an I/O feature is not allowed to be
         * another I/O feature. This is to prevent possible headaches
         * and confusion that would result from doing such a thing.
         */
        feature.containerState = new MockIoFeature();
        assertThrows(UnsupportedOperationException.class,
                () -> feature.getState());

        /* use valid container state for next test */
        feature.containerState = new Object();

        /*
         * Once valid states are returned by the I/O feature, the state
         * container returned should contain the states provided by the
         * extending class.
         */
        StatePair<?, ?> pair = feature.getState();
        assertNotNull(pair);
        assertSame(pair.internal, feature.internalState);
        assertSame(pair.container, feature.containerState);
    }

    @Test
    void testUpdate() {
        /*
         * By default, this method does nothing. As such, even though both
         * parameters are marked with @NotNull, nothing should be thrown.
         */
        assertDoesNotThrow(() -> feature.update(null, null));
    }

}