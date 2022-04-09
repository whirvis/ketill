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
    void setup() {
        this.event = mock(HidServicesEvent.class);
        this.seeker = mock(MockHidDeviceSeeker.class);
        this.listener = new HidDeviceListener(seeker);
    }

    @Test
    void hidDeviceAttached() {
        listener.hidDeviceAttached(event);
        verify(seeker).hidDeviceAttached(event);
    }

    @Test
    void hidDeviceDetached() {
        listener.hidDeviceDetached(event);
        verify(seeker).hidDeviceDetached(event);
    }

    @Test
    void hidFailure() {
        listener.hidFailure(event);
        verify(seeker).hidFailure(event);
    }

}
