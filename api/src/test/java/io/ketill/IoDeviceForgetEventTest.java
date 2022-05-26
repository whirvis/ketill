package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IoDeviceForgetEventTest {

    private IoDevice device;
    private IoDeviceForgetEvent event;

    @BeforeEach
    void createEvent() {
        IoDeviceSeeker<?> seeker = mock(IoDeviceSeeker.class);
        this.device = mock(IoDevice.class);
        this.event = new IoDeviceForgetEvent(seeker, device);
    }

    @Test
    void testGetDevice() {
        assertSame(device, event.getDevice());
    }

}
