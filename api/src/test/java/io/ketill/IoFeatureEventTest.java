package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoFeatureEventTest {

    private IoDevice device;
    private IoFeature<?, ?> feature;
    private MockIoFeatureEvent event;

    @BeforeEach
    void createEvent() {
        this.device = mock(IoDevice.class);
        this.feature = new MockIoFeature();
        this.event = new MockIoFeatureEvent(device, feature);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new IoFeatureRegisterEvent(device, null));
    }

    @Test
    void testGetFeature() {
        assertSame(feature, event.getFeature());
    }

}
