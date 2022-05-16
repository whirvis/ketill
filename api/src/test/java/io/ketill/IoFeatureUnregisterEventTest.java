package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class IoFeatureUnregisterEventTest {

    private IoDevice device;
    private IoFeature<?, ?> feature;
    private IoFeatureUnregisterEvent event;

    @BeforeEach
    void createEvent() {
        this.device = mock(IoDevice.class);
        this.feature = new MockIoFeature();
        this.event = new IoFeatureUnregisterEvent(device, feature);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new IoFeatureUnregisterEvent(device, null));
    }

    @Test
    void testGetFeature() {
        assertSame(feature, event.getFeature());
    }

}
