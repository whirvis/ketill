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

    private MockDeviceGuids dfltGuids;
    private MockDeviceGuids noGuids;

    @BeforeEach
    void setup() {
        this.dfltGuids = new MockDeviceGuids();
        this.noGuids = new MockDeviceGuids(false);
    }

    @Test
    void supportsSystem() {
        /*
         * It makes no sense for a system with a null ID to be
         * supported by a set of device GUIDs. As such, assume
         * this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> dfltGuids.supportsSystem(null));

        /*
         * The mock device GUID container was constructed with
         * support for the default systems enabled. As such, it
         * should support Windows, Linux, Mac OSX, and Android.
         */
        assertTrue(dfltGuids.supportsSystem(DeviceGuids.ID_WINDOWS));
        assertTrue(dfltGuids.supportsSystem(DeviceGuids.ID_LINUX));
        assertTrue(dfltGuids.supportsSystem(DeviceGuids.ID_MAC_OSX));
        assertTrue(dfltGuids.supportsSystem(DeviceGuids.ID_ANDROID));

        /*
         * Ensure that the device GUID container returns false
         * for an unsupported operating system, like iOS.
         */
        assertFalse(dfltGuids.supportsSystem("ios"));

        /*
         * When a device GUID container is constructed with the
         * argument for useDefaultSystems being false, it should
         * not support any of the default systems until they are
         * explicitly added by the user.
         */
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_WINDOWS));
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_LINUX));
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_MAC_OSX));
        assertFalse(noGuids.supportsSystem(DeviceGuids.ID_ANDROID));
    }

    @Test
    void addSystem() {
        /*
         * It would not make sense to add an operating system with
         * a null ID or a null determinant. As such, assume these
         * were mistakes by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> dfltGuids.addSystem(null, () -> false));
        assertThrows(NullPointerException.class,
                () -> dfltGuids.addSystem("dummy", null));

        /*
         * Empty IDs or IDs containing whitespace are not allowed
         * for operating system IDs. As such, throw an exception
         * if the user tries to use them.
         */
        assertThrows(IllegalArgumentException.class,
                () -> dfltGuids.addSystem("", () -> false));
        assertThrows(IllegalArgumentException.class,
                () -> dfltGuids.addSystem("\t", () -> false));

        /*
         * In the event two operating systems say they are the
         * current operating system, the device GUID container
         * must throw an exception to indicate the user of the
         * issue. Not doing so would lead to confusing and hard
         * to debug OS-specific issues.
         *
         * Furthermore, the added OS which caused the conflict
         * should not be kept as a supported operating system.
         */
        assertThrows(IllegalStateException.class,
                () -> dfltGuids.addSystem("dummy", () -> true));
        assertFalse(dfltGuids.supportsSystem("dummy"));
    }

    @Test
    void removeSystem() {
        /*
         * It would not make sense to remove an operating system
         * with a null ID. As such, assume this was a mistake by
         * the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> dfltGuids.removeSystem(null));

        /*
         * When an operating system which was previously added is
         * removed from the device GUID container, a value of true
         * must be returned. If it was not previously added, then
         * a value of false must be returned.
         */
        assertTrue(dfltGuids.removeSystem(DeviceGuids.ID_WINDOWS));
        assertFalse(dfltGuids.removeSystem(DeviceGuids.ID_WINDOWS));
    }

    @Test
    void getGuids() {
        /*
         * It would not make sense to fetch the GUIDs for an
         * operating system with a null ID. Assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> dfltGuids.getGuids(null));

        /*
         * When there are no GUIDs present for an operating
         * system, the device GUID container must return null
         * rather than an empty collection.
         */
        assertNull(dfltGuids.getGuids("dummy"));

        /* randomly generate GUIDs for next test */
        Random random = new Random();
        String[] generatedIds = new String[32];
        for (int i = 0; i < generatedIds.length; i++) {
            int guid = random.nextInt();
            generatedIds[i] = Integer.toHexString(guid);
        }
        dfltGuids.currentGuids = generatedIds;

        /* randomly generate system ID for next test */
        int systemId = random.nextInt();
        String systemIdStr = Integer.toHexString(systemId);
        dfltGuids.currentSystemId = systemIdStr;

        /*
         * Ensure all the randomly generated IDs for the randomly
         * generated system ID are returned. If any are missing,
         * something has gone wrong internally.
         */
        Collection<String> fetched = dfltGuids.getGuids(systemIdStr);
        assertNotNull(fetched);
        for (String generatedId : generatedIds) {
            assertTrue(fetched.contains(generatedId));
        }

        /*
         * Modifying the returned GUID container is illegal. If the
         * user attempts to do so, an exception should be thrown.
         */
        assertThrows(UnsupportedOperationException.class, fetched::clear);
    }

    @Test
    void getSystemGuids() {
        noGuids.currentSystemId = "dummy";
        noGuids.currentGuids = new String[0];

        /*
         * With a dummy system added, the GUID container can now
         * determine the current operating system. A call to the
         * internal getGuidsImpl() should be made and a non-null
         * value must be returned.
         */
        noGuids.getGuidsImplCallCount = 0;
        noGuids.addSystem("dummy", () -> true);
        assertNotNull(noGuids.getSystemGuids());
        assertEquals(1, noGuids.getGuidsImplCallCount);

        /*
         * With the dummy system removed, the GUID container is
         * now unable to determine the current operating system.
         * As such, it should not make a call to getGuidsImpl()
         * and should return a value of null.
         */
        noGuids.getGuidsImplCallCount = 0;
        noGuids.removeSystem("dummy");
        assertNull(noGuids.getSystemGuids());
        assertEquals(0, noGuids.getGuidsImplCallCount);
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void getSystemGuidsWindows() {
        /*
         * When running on Windows, ensure that getSystemGuids()
         * results in the GUIDs for Windows being requested.
         */
        dfltGuids.getSystemGuids(); /* set guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_WINDOWS, dfltGuids.lastRequestedOs);
    }

    @Test
    @EnabledOnOs(OS.MAC)
    void getSystemGuidsMacOSX() {
        /*
         * When running on Mac OSX, ensure that getSystemGuids()
         * results in the GUIDs for Mac OSX being requested.
         */
        dfltGuids.getSystemGuids(); /* set guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_MAC_OSX, dfltGuids.lastRequestedOs);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void getSystemGuidsLinux() {
        /*
         * When running on Linux, ensure that getSystemGuids()
         * results in the GUIDs for Linux being requested.
         */
        dfltGuids.getSystemGuids(); /* set guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_LINUX, dfltGuids.lastRequestedOs);
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void getSystemGuidsAndroid() {
        /*
         * Android runs on Linux, however that is not specific
         * enough for the GUID container (since devices have
         * different GUIDs on Android than they do on Linux).
         * This ensures the unit test is running in an Android
         * environment before continuing.
         */
        String runtimeName = System.getProperty("java.runtime.name");
        assumeTrue(runtimeName != null);
        assumeTrue(runtimeName.toLowerCase().contains("android"));

        /*
         * When running on Android, ensure that getSystemGuids()
         * results in the GUIDs for Android being requested.
         */
        dfltGuids.getSystemGuids(); /* set guids.lastRequestedOs */
        assertEquals(DeviceGuids.ID_ANDROID, dfltGuids.lastRequestedOs);
    }

}