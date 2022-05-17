package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IoFeatureRegisterEventTest {

    private RegisteredFeature<?, ?, ?> registered;
    private IoFeatureRegisterEvent event;

    @BeforeEach
    void createEvent() {
        IoDeviceObserver events = mock(IoDeviceObserver.class);
        IoDevice device = mock(IoDevice.class);
        IoFeature<?, ?> feature = new MockIoFeature();

        this.registered = new RegisteredFeature<>(feature, events);
        this.event = new IoFeatureRegisterEvent(device, registered);
    }

    @Test
    void testGetRegistration() {
        assertSame(registered, event.getRegistration());
    }

}
