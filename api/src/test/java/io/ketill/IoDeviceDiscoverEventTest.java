package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceDiscoverEventTest {

    private IoDeviceSeeker<?> seeker;
    private IoDevice device;
    private IoDeviceDiscoverEvent event;

    @BeforeEach
    void createEvent() {
        this.seeker = mock(IoDeviceSeeker.class);
        this.device = mock(IoDevice.class);
        this.event = new IoDeviceDiscoverEvent(seeker, device);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new IoDeviceDiscoverEvent(seeker, null));
    }

    @Test
    void testGetDevice() {
        assertSame(device, event.getDevice());
    }

}
