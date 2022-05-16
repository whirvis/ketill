package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceForgetEventTest {

    private IoDeviceSeeker<?> seeker;
    private IoDevice device;
    private IoDeviceForgetEvent event;

    @BeforeEach
    void createEvent() {
        this.seeker = mock(IoDeviceSeeker.class);
        this.device = mock(IoDevice.class);
        this.event = new IoDeviceForgetEvent(seeker, device);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new IoDeviceForgetEvent(seeker, null));
    }

    @Test
    void testGetDevice() {
        assertSame(device, event.getDevice());
    }

}
