package io.ketill.hidusb;

import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;

/**
 * This wrapper class exists only to ensure that the callback methods of
 * {@link HidServicesListener} cannot be called by other classes.
 */
final class HidDeviceListener implements HidServicesListener {

    private final HidDeviceSeeker<?> seeker;

    HidDeviceListener(HidDeviceSeeker<?> seeker) {
        this.seeker = seeker;
    }

    @Override
    public void hidDeviceAttached(HidServicesEvent event) {
        seeker.hidDeviceAttached(event);
    }

    @Override
    public void hidDeviceDetached(HidServicesEvent event) {
        seeker.hidDeviceDetached(event);
    }

    @Override
    public void hidFailure(HidServicesEvent event) {
        seeker.hidFailure(event);
    }

}
