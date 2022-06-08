package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import io.ketill.IoDeviceDiscoverEvent;
import io.ketill.IoDeviceForgetEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Even though these tests for X-input use heavy mocking, they will
 * only work on Windows due to how the native libraries are loaded.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@EnabledOnOs(OS.WINDOWS)
class XInputXboxSeekerTest {

    private XInputDevice[] devices;
    private XInputXboxSeeker seeker;

    @BeforeEach
    void createSeeker() {
        /*
         * For some reason, an EXCEPTION_ACCESS_VIOLATION will be raised
         * if the static mocks are not created in this order. Maybe it has
         * to do with how the native libraries of JXInput are loaded?
         */
        MockedStatic<XInputDevice14> x14 = mockStatic(XInputDevice14.class);
        MockedStatic<XInputDevice> x10 = mockStatic(XInputDevice.class);

        /*
         * For these tests, the earliest version of X-input available on
         * this machine will be used. This mock prevents the seeker from
         * detecting that X-input 1.4 is available.
         */
        x14.when(XInputDevice14::isAvailable).thenReturn(false);
        x10.when(XInputDevice::isAvailable).thenReturn(true);

        /*
         * The code below creates an array of mock X-input devices which
         * can be polled without any errors occurring. Minimum mocking is
         * performed here, as only the seeker is being tested.
         */
        this.devices = new XInputDevice[XInput.PLAYER_COUNT];
        for (int i = 0; i < devices.length; i++) {
            XInputComponents comps = mock(XInputComponents.class);
            when(comps.getButtons()).thenCallRealMethod();
            when(comps.getAxes()).thenCallRealMethod();

            XInputDevice device = mock(XInputDevice.class);
            when(device.isConnected()).thenReturn(false);
            when(device.getComponents()).thenReturn(comps);

            this.devices[i] = device;
        }

        /*
         * Before creating the seeker, trigger the controller cache to
         * be loaded so our mock devices will be used instead. It will
         * not be possible to do this after creating the seeker.
         *
         * Notice here how getPlayer(), instead of cacheDevices(), is
         * used to load the devices into memory. This is intentional!
         * Using latter will result in the devices being loaded again
         * once getPlayer() is called. This is by design, since other
         * tests need to call cacheDevices() multiple times.
         */
        x10.when(XInputDevice::getAllDevices).thenReturn(devices);
        XInput.getPlayer(0); /* trigger permanent cache load */

        this.seeker = new XInputXboxSeeker();

        x10.close(); /* stop mocking X-input v1.0 */
        x14.close(); /* stop mocking X-input v1.4 */
    }

    @Test
    void testSeekImpl() {
        XInputDevice player1 = this.devices[0];

        AtomicBoolean discovered = new AtomicBoolean();
        seeker.subscribeEvents(IoDeviceDiscoverEvent.class,
                event -> discovered.set(true));

        /*
         * Device connection is mocked to stimulate the device seeker
         * into discovering a device. Once the seeker sees the device
         * has been connected, it should be discovered.
         */
        when(player1.isConnected()).thenReturn(true);
        seeker.seek(); /* trigger discover event */
        assertTrue(discovered.get());

        AtomicBoolean forgotten = new AtomicBoolean();
        seeker.subscribeEvents(IoDeviceForgetEvent.class,
                event -> forgotten.set(true));

        /*
         * Device disconnection is mocked to stimulate the device seeker
         * into forgetting a device. Once the seeker sees the device has
         * been disconnected, it should be forgotten.
         */
        when(player1.isConnected()).thenReturn(false);
        seeker.seek(); /* trigger forget event */
        assertTrue(forgotten.get());
    }

}
