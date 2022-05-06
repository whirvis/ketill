package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
@EnabledOnOs(OS.WINDOWS)
class XInputXboxSeekerTest {

    private XInputXboxSeeker seeker;

    @BeforeEach
    void createSeeker() {
        /*
         * For these tests, the earliest version of X-input available on this
         * machine will be used. This mock prevents the seeker from detecting
         * that X-input 1.4 is available.
         */
        try (MockedStatic<XInputDevice14> x14 =
                     mockStatic(XInputDevice14.class)) {
            x14.when(XInputDevice14::isAvailable).thenReturn(false);
            this.seeker = new XInputXboxSeeker();
        }
    }

    @Test
    void testSeekImpl() {
        try (MockedStatic<XInputDevice> x = mockStatic(XInputDevice.class)) {
            /*
             * Only one device is going to be considered connected for this
             * test. As such, a disconnected device must be mocked so device
             * seeker doesn't get a null device instance.
             */
            XInputDevice xDisconnected = mock(XInputDevice.class);
            when(xDisconnected.isConnected()).thenReturn(false);
            x.when(() -> XInputDevice.getDeviceFor(anyInt()))
                    .thenReturn(xDisconnected);

            /*
             * The connected device is mocked to stimulate the device seeker
             * into discovering a device. This allows for the functionality
             * of its discovery to be tested.
             */
            XInputDevice xDevice = mock(XInputDevice.class);
            when(xDevice.isConnected()).thenReturn(true);
            x.when(() -> XInputDevice.getDeviceFor(0)).thenReturn(xDevice);

            AtomicBoolean discovered = new AtomicBoolean();
            seeker.onDiscoverDevice((s, d) -> discovered.set(true));
            seeker.seek();
            assertTrue(discovered.get());

            /*
             * All but one of the mocked devices are currently considered
             * to be disconnected. As such, the seeker should have invoked
             * poll() for each of them. Polling a device is required to
             * determine its connection status.
             */
            int disconnectedCount = XInputConstants.MAX_PLAYERS - 1;
            verify(xDisconnected, times(disconnectedCount)).poll();

            when(xDevice.isConnected()).thenReturn(false);
            AtomicBoolean forgotten = new AtomicBoolean();
            seeker.onForgetDevice((s, d) -> forgotten.set(true));
            seeker.seek();
            assertTrue(forgotten.get());
        }
    }

}