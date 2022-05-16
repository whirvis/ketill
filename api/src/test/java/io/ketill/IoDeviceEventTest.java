package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.Null;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceEventTest {

    private IoDevice device;
    private MockIoDeviceEvent event;

    @BeforeEach
    void createEvent() {
        this.device = mock(IoDevice.class);
        this.event = new MockIoDeviceEvent(device);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new MockIoDeviceEvent(null));
    }

    @Test
    void getDevice() {
        assertSame(device, event.getDevice());
    }

}
