package io.ketill.hidusb;

import org.hid4java.event.HidServicesEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class HidDeviceListenerTest {

    private HidServicesEvent event;
    private MockHidDeviceSeeker seeker;
    private HidDeviceListener listener;

    @BeforeEach
    void createListener() {
        this.event = mock(HidServicesEvent.class);
        this.seeker = mock(MockHidDeviceSeeker.class);
        this.listener = new HidDeviceListener(seeker);
    }

    @Test
    void testHidDeviceAttached() {
        listener.hidDeviceAttached(event);
        verify(seeker).hidDeviceAttached(event);
    }

    @Test
    void testHidDeviceDetached() {
        listener.hidDeviceDetached(event);
        verify(seeker).hidDeviceDetached(event);
    }

    @Test
    void testHidFailure() {
        listener.hidFailure(event);
        verify(seeker).hidFailure(event);
    }

}
