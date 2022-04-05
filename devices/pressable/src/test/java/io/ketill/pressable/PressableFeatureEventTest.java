package io.ketill.pressable;

import io.ketill.IoDevice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class PressableFeatureEventTest {

    @Test
    void __init__() {
        PressableFeatureEventType type = PressableFeatureEventType.PRESS;
        IoDevice device = mock(IoDevice.class);
        MockIoFeature feature = new MockIoFeature();
        Object state = new Object();
        boolean held = false;
        Object data = new Object();

        /*
         * It would not make sense to fire an event with
         * a null type, device, or feature. Assume these
         * were user mistakes and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new PressableFeatureEvent(null, device, feature, held,
                        data));
        assertThrows(NullPointerException.class,
                () -> new PressableFeatureEvent(type, null, feature, held,
                        data));
        assertThrows(NullPointerException.class,
                () -> new PressableFeatureEvent(type, device, null, held,
                        data));

        /*
         * The PressableFeatureEvent constructor grabs the
         * state of the given feature from the device. As
         * such, it must be mocked to prevent an exception
         * from being thrown by the constructor.
         */
        when(device.getState(feature)).thenReturn(state);

        PressableFeatureEvent event = new PressableFeatureEvent(type, device,
                feature, held, data);

        /*
         * Ensure that all fields are the same arguments
         * that were passed during construction. This is
         * more or less just a sanity check.
         */
        assertSame(event.type, type);
        assertSame(event.device, device);
        assertSame(event.feature, feature);
        assertSame(event.state, state);
        assertSame(event.held, held);
        assertSame(event.data, data);
    }

}
