package io.ketill.pc;

import io.ketill.IoDeviceEvent;
import io.ketill.IoDeviceObserver;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EventAssertions {

    /* @formatter:off */
    static void assertEmitted(IoDeviceObserver observer,
                              Class<? extends IoDeviceEvent> type) {
        verify(observer).onNext(argThat(event ->
                event.getDevice() == observer.getDevice()
                    && event.getClass().isAssignableFrom(type)));
    }
    /* @formatter:on */

}
