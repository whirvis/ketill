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
        IoDevice device = mock(IoDevice.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(device);
        IoFeature<?, ?> feature = new MockIoFeature();

        this.registered = new RegisteredFeature<>(feature, observer);
        this.event = new IoFeatureRegisterEvent(device, registered);
    }

    @Test
    void testGetRegistration() {
        assertSame(registered, event.getRegistration());
    }

}
