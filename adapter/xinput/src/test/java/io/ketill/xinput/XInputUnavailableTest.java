package io.ketill.xinput;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These tests ensure that it is possible for systems not running on
 * Windows to at least check if X-input is available without errors.
 */
@DisabledOnOs(OS.WINDOWS)
class XInputUnavailableTest {

    @Test
    void testIsAvailable() {
        assertFalse(XInput.isAvailable());
    }

    @Test
    void testRequireAvailable() {
        assertThrows(XInputUnavailableException.class,
                XInput::requireAvailable);
    }

    @Test
    void testGetVersion() {
        assertNull(XInput.getVersion());
    }

    @Test
    void testIsAtLeast() {
        assertFalse(XInput.isAtLeast(XInputVersion.V1_0));
    }

    @Test
    void testRequireAtLeast() {
        assertThrows(XInputUnavailableException.class,
                () -> XInput.requireAtLeast(XInputVersion.V1_0));
    }

    @Test
    void testSetEnabled() {
        assertThrows(XInputUnavailableException.class,
                () -> XInput.setEnabled(false));
    }

    @Test
    void testTrySetEnabled() {
        assertFalse(XInput.trySetEnabled(false));
    }

    @Test
    void testGetPlayer() {
        assertThrows(XInputUnavailableException.class,
                () -> XInput.getPlayer(0));
    }

    @Test
    void testGetAllPlayers() {
        assertThrows(XInputUnavailableException.class, XInput::getAllPlayers);
    }

    @Test
    void createSeeker() {
        assertThrows(XInputUnavailableException.class, XInputXboxSeeker::new);
    }

}
