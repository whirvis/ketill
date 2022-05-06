package io.ketill.glfw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.Collection;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

@SuppressWarnings("ConstantConditions")
class DeviceGuidsTest {

    private MockDeviceGuids defaultGuids;
    private MockDeviceGuids noGuids;

    @BeforeEach
    void createContainer() {
        this.defaultGuids = new MockDeviceGuids();
        this.noGuids = new MockDeviceGuids(false);
    }

    @Test
    void testSupportsSystem() {
        /*
         * It makes no sense for a system with a null ID to be supported by
         * a set of device GUIDs. As such, assume this was a mistake by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> defaultGuids.supportsSystem(null));

        /*
         * The mock device GUID container was constructed with support for
         * the default systems enabled. As such, it should support Windows,
         * Linux, Mac OSX, and Android.
         */
        assertTrue(defaultGuids.supportsSystem(DeviceGuids.ID_WINDOWS));
        assertTrue(defaultGuids.supportsSystem(DeviceGuids.ID_LINUX));
        assertTrue(defaultGuids.supportsSystem(DeviceGuids.ID_MAC_OSX));
        assertTrue(defaultGuids.supportsSystem(DeviceGuids.ID_ANDROID));

        /*
         * Ensure that the device GUID container returns false for an
         * unsupported operating system, like iOS.
         */
        assertFalse(defaultGuids.supportsSystem("ios"));

        /*
         * When a device GUID container is constructed with the argument for
         * useDefaultSystems being false, it should not support any of the
         * default systems until they are explicitly added by the user.
         */
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_WINDOWS));
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_LINUX));
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_MAC_OSX));
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_ANDROID));
    }

    @Test
    void testAddSystem() {
        /*
         * It would not make sense to add an operating system with a null ID
         * or a null determinant. As such, assume these were mistakes by the
         *  user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> defaultGuids.addSystem(null, () -> false));
        assertThrows(NullPointerException.class,
                () -> defaultGuids.addSystem("dummy", null));

        /*
         * Empty IDs or IDs containing whitespace are not allowed for
         * operating system IDs. As such, throw an exception if the user
         * tries to use them.
         */
        assertThrows(IllegalArgumentException.class,
                () -> defaultGuids.addSystem("", () -> false));
        assertThrows(IllegalArgumentException.class,
                () -> defaultGuids.addSystem("\t", () -> false));

        /*
         * In the event two operating systems say they are the current OS,
         * the device GUID container must throw an exception to indicate
         * the user of the issue. Not doing so would result in confusing
         * and hard to debug OS-specific issues.
         *
         * Furthermore, the added OS which caused the conflict should not
         * be kept as a supported operating system.
         */
        assertThrows(IllegalStateException.class,
                () -> defaultGuids.addSystem("dummy", () -> true));
        assertFalse(defaultGuids.supportsSystem("dummy"));
    }

    @Test
    void testRemoveSystem() {
        /*
         * It would not make sense to remove an operating system with a
         * null ID. As such, assume this was a mistake by the user and
         * throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> defaultGuids.removeSystem(null));

        /*
         * When an operating system previously added is removed from the
         * device GUID container, a value of true must be returned. If it
         * was not previously added, then it must be a value of false.
         */
        assertTrue(defaultGuids.removeSystem(DeviceGuids.ID_WINDOWS));
        assertFalse(defaultGuids.removeSystem(DeviceGuids.ID_WINDOWS));
    }

    @Test
    void testGetGuids() {
        /*
         * It would not make sense to fetch the GUIDs for an operating
         * system with a null ID. Assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> defaultGuids.getGuids(null));

        /*
         * When there are no GUIDs present for an operating system, the
         * device GUID container must return null, not an empty container.
         */
        assertNull(defaultGuids.getGuids("dummy"));

        /* randomly generate GUIDs for next test */
        Random random = new Random();
        String[] generatedIds = new String[32];
        for (int i = 0; i < generatedIds.length; i++) {
            int guid = random.nextInt();
            generatedIds[i] = Integer.toHexString(guid);
        }
        defaultGuids.currentGuids = generatedIds;

        /* randomly generate system ID for next test */
        int systemId = random.nextInt();
        String systemIdStr = Integer.toHexString(systemId);
        defaultGuids.currentSystemId = systemIdStr;

        /*
         * Ensure that all randomly generated IDs for the random system
         * ID are returned. If any are missing, something has gone wrong.
         */
        Collection<String> fetched = defaultGuids.getGuids(systemIdStr);
        assertNotNull(fetched);
        for (String generatedId : generatedIds) {
            assertTrue(fetched.contains(generatedId));
        }

        /*
         * The user should not be able to modify the returned GUID container.
         * If they attempt to do so, an exception should be thrown.
         */
        assertThrows(UnsupportedOperationException.class, fetched::clear);
    }

    @Test
    void testGetSystemGuids() {
        noGuids.currentSystemId = "dummy";
        noGuids.currentGuids = new String[0];

        /*
         * With a dummy system added, the GUID container can now determine
         * the current operating system. A call to getGuidsImpl() should be
         * made and a non-null value must be returned.
         */
        noGuids.getGuidsImplCallCount = 0;
        noGuids.addSystem("dummy", () -> true);
        assertNotNull(noGuids.getSystemGuids());
        assertEquals(1, noGuids.getGuidsImplCallCount);

        /*
         * With the dummy system removed, the GUID container is now unable
         * to determine the current operating system. As such, it should not
         * make a call to getGuidsImpl() and should return a value of null.
         */
        noGuids.getGuidsImplCallCount = 0;
        noGuids.removeSystem("dummy");
        assertNull(noGuids.getSystemGuids());
        assertEquals(0, noGuids.getGuidsImplCallCount);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testGetSystemGuidsOnWindows() {
        defaultGuids.getSystemGuids(); /* sets guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_WINDOWS, defaultGuids.lastRequestedOs);
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void testGetSystemGuidsOnMacOSX() {
        defaultGuids.getSystemGuids(); /* sets guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_MAC_OSX, defaultGuids.lastRequestedOs);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testGetSystemGuidsOnLinux() {
        defaultGuids.getSystemGuids(); /* sets guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_LINUX, defaultGuids.lastRequestedOs);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void testGetSystemGuidsOnAndroid() {
        /*
         * Android runs on Linux, however that is not specific enough for the
         * GUID container (since devices have different GUIDs on Android than
         * they do on Linux). This ensures the test is running in an Android
         * environment before continuing.
         */
        String runtimeName = System.getProperty("java.runtime.name");
        assumeTrue(runtimeName != null);
        assumeTrue(runtimeName.toLowerCase().contains("android"));

        defaultGuids.getSystemGuids(); /* sets guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_ANDROID, defaultGuids.lastRequestedOs);
    }

}