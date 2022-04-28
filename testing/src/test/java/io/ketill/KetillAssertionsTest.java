package io.ketill;

import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class KetillAssertionsTest {

    @Test
    void assertStateOwnsFeature() {
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
