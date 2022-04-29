package io.ketill;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class KetillAssertionsTest {

    @Test
    void testAssertAllFeaturesSupported() {
        IoDevice device = mock(IoDevice.class);
        MockIoFeature feature = new MockIoFeature();

        /*
         * It would not make sense to check if all registered features are
         * supported on a null device, to pass a null array of unsupported
         * features, or pass a null unsupported feature. As such, assume
         * these were mistakes by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> assertAllFeaturesSupported(null));
        assertThrows(NullPointerException.class,
                () -> assertAllFeaturesSupported(device,
                        (IoFeature<?, ?>[]) null));
        assertThrows(NullPointerException.class,
                () -> assertAllFeaturesSupported(device,
                        new IoFeature<?, ?>[]{null}));

        /*
         * Since the unsupported feature passes is not even registered to
         * the device, it is assumed this was a mistake. The goal of this
         * method is to check if registered features are supported.
         */
        assertThrows(AssertionError.class,
                () -> assertAllFeaturesSupported(device, feature));

        RegisteredFeature<MockIoFeature, Object, Object> registered =
                new RegisteredFeature<>(feature);
        Collection<RegisteredFeature<?, ?, ?>> features =
                Collections.singletonList(registered);

        /* mock registration and mapping of feature */
        doReturn(features).when(device).getFeatures();
        doReturn(true).when(device).isFeatureRegistered(feature);
        doReturn(true).when(device).isFeatureSupported(feature);

        /*
         * Since the feature is now registered and supported, it would not
         * make sense to pass it as an unsupported feature. Assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(AssertionError.class,
                () -> assertAllFeaturesSupported(device, feature));

        /*
         * The mock below causes the I/O device to report the feature is not
         * supported. As such, this method should throw an AssertionError.
         */
        doReturn(false).when(device).isFeatureSupported(feature);
        assertThrows(AssertionError.class,
                () -> assertAllFeaturesSupported(device));

        /*
         * The mock below causes the I/O device to report the feature is now
         * supported. As such, the assertion should pass.
         */
        doReturn(true).when(device).isFeatureSupported(feature);
        assertAllFeaturesSupported(device);
    }

    @Test
    void testAssertFeatureOwnsState() {
        IoDevice device = mock(IoDevice.class);
        MockIoFeature feature = new MockIoFeature();
        Object state = new Object();

        /*
         * It would not make sense to check if a feature owns a state on a
         * null device, if a feature owns a null state, or a null feature
         * owns a state. As such, assume these were mistakes by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> assertFeatureOwnsState(null, state, feature));
        assertThrows(NullPointerException.class,
                () -> assertFeatureOwnsState(device, null, feature));
        assertThrows(NullPointerException.class,
                () -> assertFeatureOwnsState(device, state, null));

        /*
         * Since the feature is not registered to the device, it therefore
         * cannot own any state object provided to it. As such, this method
         * should throw an AssertionError.
         */
        assertThrows(AssertionError.class,
                () -> assertFeatureOwnsState(device, state, feature));

        /*
         * The mock below causes the I/O device to report that the feature
         * belong to state is the mock I/O feature created above. As such,
         * the assertion should pass.
         */
        doReturn(feature).when(device).getFeature(state);
        assertFeatureOwnsState(device, state, feature);
    }

}
