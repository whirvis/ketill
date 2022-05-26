package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IoDeviceDiscoverEventTest {

    private IoDevice device;
    private IoDeviceDiscoverEvent event;

    @BeforeEach
    void createEvent() {
        IoDeviceSeeker<?> seeker = mock(IoDeviceSeeker.class);
        this.device = mock(IoDevice.class);
        this.event = new IoDeviceDiscoverEvent(seeker, device);
    }

    @Test
    void testGetDevice() {
        assertSame(device, event.getDevice());
    }

}
