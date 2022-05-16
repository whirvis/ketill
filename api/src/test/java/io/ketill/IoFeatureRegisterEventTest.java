package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoFeatureRegisterEventTest {

    private IoDevice device;
    private IoFeature<?, ?> feature;
    private RegisteredFeature<?, ?, ?> registered;
    private IoFeatureRegisterEvent event;

    @BeforeEach
    void createEvent() {
        IoDeviceObserver events = mock(IoDeviceObserver.class);

        this.device = mock(IoDevice.class);
        this.feature = new MockIoFeature();
        this.registered = new RegisteredFeature<>(feature, events);
        this.event = new IoFeatureRegisterEvent(device, registered);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new IoFeatureRegisterEvent(device, null));
    }

    @Test
    void testGetRegistration() {
        assertSame(registered, event.getRegistration());
    }

    @Test
    void testGetFeature() {
        assertSame(feature, event.getFeature());
    }

}
