package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.XInputLibraryVersion;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
class XInputTest {

    private static MockedStatic<XInputDevice14> x14;
    private static MockedStatic<XInputDevice> x10;

    @BeforeAll
    static void startMockingXInput() {
        /*
         * For some reason, an EXCEPTION_ACCESS_VIOLATION will be raised
         * if the static mocks are not created in this order. Maybe it has
         * to do with how the native libraries of JXInput are loaded?
         */
        x14 = mockStatic(XInputDevice14.class);
        x10 = mockStatic(XInputDevice.class);
    }

    @BeforeEach
    void setup() {
        x14.reset();
        x10.reset();

        /* mock devices for later tests */
        XInputDevice14[] devices = new XInputDevice14[XInput.PLAYER_COUNT];
        for (int i = 0; i < devices.length; i++) {
            devices[i] = mock(XInputDevice14.class);
        }

        x14.when(XInputDevice14::getAllDevices).thenReturn(devices);
        x10.when(XInputDevice::getAllDevices).thenReturn(devices);

        XInput.reset();
    }

    @Test
    void validatePlayerCount() {
        assertEquals(XInputConstants.MAX_PLAYERS, XInput.PLAYER_COUNT);
    }

    @Test
    void testIsAvailable() {
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        assertTrue(XInput.isAvailable());
        x10.when(XInputDevice::isAvailable).thenReturn(false);
        assertFalse(XInput.isAvailable());
    }

    @Test
    void testRequireAvailable() {
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        assertDoesNotThrow(XInput::requireAvailable);
        x10.when(XInputDevice::isAvailable).thenReturn(false);
        assertThrows(XInputUnavailableException.class,
                XInput::requireAvailable);
    }

    @Test
    void testGetVersion() {
        /*
         * When X-input is not available on the current machine, this method
         * should return null for the library version. This lets the caller
         * know that X-input isn't available in the first place.
         */
        x10.when(XInputDevice::isAvailable).thenReturn(false);
        assertNull(XInput.getVersion());
        x10.when(XInputDevice::isAvailable).thenReturn(true);

        /* @formatter:off */
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_9_1_0);
        assertEquals(XInputVersion.V1_0, XInput.getVersion());
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_1_3);
        assertEquals(XInputVersion.V1_3, XInput.getVersion());
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_1_4);
        assertEquals(XInputVersion.V1_4, XInput.getVersion());
        /* @formatter:on */
    }

    @Test
    void testIsAtLeast() {
        /*
         * It would not make sense to check if the current version of
         * X-input is at least a null version. As such, assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> XInput.isAtLeast(null));

        /*
         * In the event X-input is unavailable entirely, then there is
         * no current version to be at least the version requested by
         * the user. As such, a value of false should be returned.
         */
        x10.when(XInputDevice::isAvailable).thenReturn(false);
        assertFalse(XInput.isAtLeast(XInputVersion.V1_3));

        /* mock X-input v1.3 availability for next test */
        /* @formatter:off */
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_1_3);
        /* @formatter:on */

        assertTrue(XInput.isAtLeast(XInputVersion.V1_0));
        assertFalse(XInput.isAtLeast(XInputVersion.V1_4));
    }

    @Test
    void testRequireAtLeast() {
        /*
         * It would not make sense to require at least a null version of
         * X-input to be available on this machine. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> XInput.requireAtLeast(null));

        /*
         * In the event X-input is unavailable entirely, then there is
         * no current version to be at least the version requested by
         * the user. As such, an exception should be thrown.
         */
        x10.when(XInputDevice::isAvailable).thenReturn(false);
        assertThrows(XInputVersionException.class,
                () -> XInput.requireAtLeast(XInputVersion.V1_3));

        /* mock X-input v1.3 availability for next test */
        /* @formatter:off */
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_1_3);
        /* @formatter:on */

        /*
         * The current version of X-input is mocked to be X-input v1.3.
         * As such, requiring at least X-input v1.0 (a lower version)
         * should not result in an exception being thrown.
         */
        assertDoesNotThrow(() -> XInput.requireAtLeast(XInputVersion.V1_0));

        /*
         * The current version of X-input is mocked to be X-input v1.3.
         * As such, requiring at least X-input v1.4 (a higher version)
         * should result in an exception being thrown.
         */
        assertThrows(XInputVersionException.class,
                () -> XInput.requireAtLeast(XInputVersion.V1_4));
    }

    @Test
    void testSetEnabled() {
        /*
         * Enabling / disabling X-input is only possible on X-input v1.4
         * or later. At the moment, X-input is mocked to not be available
         * on this system. As such, this should result in an exception.
         */
        assertThrows(XInputVersionException.class,
                () -> XInput.setEnabled(false));

        /* mock X-input v1.4 availability for next test */
        /* @formatter:off */
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_1_4);
        /* @formatter:on */

        /*
         * Now that the availability of X-input v1.4 is being mocked, this
         * method should not throw an exception. It should also make a call
         * to the underlying XInputDevice14 API to fulfill the request.
         */
        assertDoesNotThrow(() -> XInput.setEnabled(false));
        x14.verify(() -> XInputDevice14.setEnabled(false));
    }

    @Test
    void testTrySetEnabled() {
        /*
         * Enabling / disabling X-input is only possible on X-input v1.4
         * or later. At the moment, X-input is mocked to not be available
         * on this system. As such, this should return a value of false.
         */
        assertFalse(XInput.trySetEnabled(false));

        /* mock X-input v1.4 availability for next test */
        /* @formatter:off */
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        x10.when(XInputDevice::getLibraryVersion)
                .thenReturn(XInputLibraryVersion.XINPUT_1_4);
        /* @formatter:on */

        /*
         * Now that the availability of X-input v1.4 is being mocked, this
         * method should return a value of true. It should also make a call
         * to the underlying XInputDevice14 API to fulfill the request.
         */
        assertTrue(XInput.trySetEnabled(false));
        x14.verify(() -> XInputDevice14.setEnabled(false));
    }

    @Test
    void testCacheControllers() {
        /*
         * When X-input v1.4 is available, it should be prioritized to
         * ensure support for features like the battery level.
         */
        x14.when(XInputDevice14::isAvailable).thenReturn(true);
        XInput.cacheControllers();
        x14.verify(XInputDevice14::getAllDevices, times(1));

        /* stop mocking availability of X-input v1.4 */
        x14.when(XInputDevice14::isAvailable).thenReturn(false);

        /*
         * Otherwise, the base X-input devices should be fetched instead.
         * If these aren't used, then what else would be?
         */
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        XInput.cacheControllers();
        x10.verify(XInputDevice::getAllDevices, times(1));

        /*
         * In the event fetching the devices from X-input fails for any
         * reason, the error should be wrapped up all sweet and nice with
         * a little bow on top to be hurled back to the user.
         */
        /* @formatter:off */
        x10.when(XInputDevice::getAllDevices)
                .thenThrow(XInputNotLoadedException.class);
        assertThrows(XInputSetupException.class, XInput::cacheControllers);
        /* @formatter:on */
    }

    @Test
    void testGetPlayer() {
        /*
         * It would not make sense to get the controller for a player with
         * a negative player number or a player number that is too large.
         * As such, assume this was a user mistake and throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> XInput.getPlayer(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> XInput.getPlayer(XInput.PLAYER_COUNT));

        /*
         * At the moment, X-input is mocked to not be available on this
         * system. In this situation, getting the controller for any player
         * should be impossible. As such, an exception should be thrown.
         */
        assertThrows(XInputUnavailableException.class,
                () -> XInput.getPlayer(0));

        /* mock X-input v1.0 availability for next test */
        x10.when(XInputDevice::isAvailable).thenReturn(true);

        /* cache all available players for next test */
        XboxController[] players = new XboxController[XInput.PLAYER_COUNT];
        for (int i = 0; i < players.length; i++) {
            players[i] = XInput.getPlayer(i);
        }

        /*
         * The controllers for each player are cached in memory, as they
         * will always represent the same player. None of them should be
         * null. Furthermore, the getPlayer() method should also return
         * the same controller instance for each player.
         */
        for (int i = 0; i < players.length; i++) {
            assertNotNull(players[i]);
            assertSame(players[i], XInput.getPlayer(i));
        }

        /*
         * The controllers should have only been cached one time after
         * calling getPlayer() is called for the first time. This can
         * can be verified by checking if getAllDevices() was invoked
         * only once. Furthermore, only the devices from X-input v1.0
         * should have been fetched. This is because X-input v1.4 was
         * mocked to be unavailable in this test.
         */
        x10.verify(XInputDevice::getAllDevices, times(1));
        x14.verify(XInputDevice14::getAllDevices, never());
    }

    @Test
    void testGetPlayerError() {
        XInputException exception = new XInputException("test");

        /*
         * When an error occurs in cacheControllers(), in this case
         * due to an induced error in the underlying X-input library,
         * it should be thrown back to the user.
         */
        x10.when(XInputDevice::isAvailable).thenReturn(true);
        x10.when(XInputDevice::getAllDevices).thenThrow(exception);
        assertThrows(XInputException.class, () -> XInput.getPlayer(0));

        /* induce another exception for next test */
        AtomicReference<Throwable> caught = new AtomicReference<>();
        try {
            XInput.getPlayer(0);
        } catch (Throwable cause) {
            caught.set(cause);
        }

        /*
         * After the first call to getPlayer() fails, any subsequent
         * call should result in the original exception being thrown
         * again. This is because simply returning null to the user
         * may result in a hard to debug situation. For example, it
         * is possible the first caller got an exception and simply
         * didn't report it anywhere.
         */
        assertSame(caught.get(), exception);
    }

    @Test
    void testGetAllPlayers() {
        x10.when(XInputDevice::isAvailable).thenReturn(true);

        /*
         * This method is a shorthand for creating an array of all
         * available players in X-input. As such, every element in
         * the array should be the same instance as the controller
         * returned by getPlayer(), with its array index being the
         * corresponding player number.
         */
        XboxController[] players = XInput.getAllPlayers();
        for (int i = 0; i < XInput.PLAYER_COUNT; i++) {
            assertSame(players[i], XInput.getPlayer(i));
        }

        /*
         * However, the array returned by this method should not
         * be a cached value. This is because the user is able to
         * modify the returned array. Calling this method again
         * should have a different array instance returned.
         */
        assertNotSame(players, XInput.getAllPlayers());
    }

    @AfterAll
    static void stopMockingXInput() {
        x10.close(); /* stop mocking X-input v1.0 */
        x14.close(); /* stop mocking X-input v1.4 */
    }

}
