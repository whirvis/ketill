package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.KetillException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.ketill.glfw.MockGlfw.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GlfwJoystickSeekerTest {

    private static final Random RANDOM = new Random();

    @BeforeAll
    static void __init__() {
        /*
         * For a GLFW joystick seeker to function, a valid
         * window pointer must be provided. As such, throw an
         * exception if the pointer is NULL or does not point
         * to a valid GLFW window.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwJoystickSeeker(0x00));
        assertThrows(IllegalArgumentException.class,
                () -> new MockGlfwJoystickSeeker(0x01));

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mockGlfwWindow(glfw, 0x01);

            /*
             * The class constructed below has an invalid use of
             * the @RelativeGuidPath annotation. As a result, an
             * exception should be thrown here.
             */
            /* @formatter:off */
            assertThrows(KetillException.class,
                    () -> new MockGlfwJoystickSeeker
                            .WithInvalidPath0(0x01));
            assertThrows(KetillException.class,
                    () -> new MockGlfwJoystickSeeker
                            .WithInvalidPath1(0x01));
            /* @formatter:on */

            /*
             * It would not make sense for the device type of GLFW
             * joystick seeker to be a null class. Assume this was
             * a mistake by the user and throw an exception.
             */
            assertThrows(NullPointerException.class,
                    () -> new MockGlfwJoystickSeeker(null, 0x01));
        }
    }

    private IoDevice device;
    private String guid;
    private GlfwJoystickWrangler<IoDevice> wrangler;

    private long ptr_glfwWindow;
    private int glfwJoystick;
    private MockGlfwJoystickSeeker seeker;

    @BeforeEach
    void setup() {
        this.device = mock(IoDevice.class);
        this.guid = Integer.toHexString(RANDOM.nextInt());
        this.wrangler = (g, w) -> device;

        /*
         * Any valid, randomly chosen pointer and GLFW joystick
         * should suffice for the following tests. The randomly
         * chosen pointer will be mocked so a mock GLFW joystick
         * seeker can be created.
         */
        this.ptr_glfwWindow = RANDOM.nextLong();
        this.glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mockGlfwWindow(glfw, ptr_glfwWindow);
            this.seeker = new MockGlfwJoystickSeeker(ptr_glfwWindow);
        }
    }

    @Test
    void loadJsonGuids() {
        /*
         * It would not make sense to try loading a JSON device
         * GUIDs container from a null path. Assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.loadJsonGuids(null));

        /*
         * There are no files located at io/ketill/glfw/ in the
         * classpath. The class also has no @RelativeGuidPath to
         * redirect the base path. As a result, this file should
         * fail to load and an exception should be thrown.
         */
        assertThrows(KetillException.class,
                () -> seeker.loadJsonGuids("valid.json"));

        /* @formatter:off */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mockGlfwWindow(glfw, ptr_glfwWindow);
            MockGlfwJoystickSeeker relative = new MockGlfwJoystickSeeker
                    .WithRelativePath(ptr_glfwWindow);

            /* ensure correct value for root path */
            assertEquals("/", RelativeGuidPath.ROOT);

            /*
             * To test @RelativeGuidPath, load the same file from
             * a GlfwJoystickSeeker which uses the annotation. It
             * has set the relative path to the folder containing
             * the file loaded via absolute path. As such, their
             * contents should match exactly.
             */
            Collection<String> guidsViaAbs =
                    seeker.loadJsonGuids("/json_device_guids/valid.json");
            Collection<String> guidsViaRelative =
                    relative.loadJsonGuids("valid.json");
            assertIterableEquals(guidsViaAbs, guidsViaRelative);
        }
        /* @formatter:on */

        /*
         * This file loads successfully, however there are no
         * known systems in this container. An exception must
         * be thrown when this occurs, as it means there won't
         * be any GUIDs to wrangle for a joystick.
         */
        assertThrows(KetillException.class, () -> seeker.loadJsonGuids(
                "/json_device_guids/alien_system.json"));
    }

    @Test
    void isWrangling() {
        /*
         * It would not make sense to check if a null GUID is
         * currently being wrangled. As such, assume this was
         * a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isWrangling(null));

        /*
         * Since the GLFW joystick seeker has not been told to
         * wrangle any GUIDs yet, this should return false.
         */
        assertFalse(seeker.isWrangling(guid));
    }

    @Test
    void isWranglingWith() {
        /*
         * It would not make sense to check if a null GUID
         * is currently being wrangled. It also makes no
         * sense to check if a GUID is being wrangled with
         * a null wrangler. Assume these were mistakes by
         * the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isWranglingWith(null, wrangler));
        assertThrows(NullPointerException.class,
                () -> seeker.isWranglingWith(guid, null));

        /*
         * Since the GLFW joystick seeker has not been told to
         * wrangle any GUIDs yet, this should return false.
         */
        assertFalse(seeker.isWranglingWith(guid, wrangler));
    }

    @Test
    void wrangleGuid() {
        /*
         * It would not make sense to wrangle a null GUID or
         * to use a null wrangler. As such, assume this was a
         * mistake by the user and throw an exception. Empty
         * GUID strings are allowed in case of oddballs.
         */
        Collection<String> guids = Collections.singletonList(guid);
        Collection<String> nullGuids = Collections.singletonList(null);
        assertThrows(NullPointerException.class,
                () -> seeker.wrangleGuid(null, wrangler));
        assertThrows(NullPointerException.class,
                () -> seeker.wrangleGuid(guid, null));
        assertThrows(NullPointerException.class,
                () -> seeker.wrangleGuids(null, wrangler));
        assertThrows(NullPointerException.class,
                () -> seeker.wrangleGuids(nullGuids, wrangler));
        assertThrows(NullPointerException.class,
                () -> seeker.wrangleGuids(guids, null));


        AtomicBoolean wrangled = new AtomicBoolean();
        seeker.onWrangleGuid((s, g, w) ->
                wrangled.set(g.equals(guid) && w == wrangler));

        /* use wrangleGuids() here for full coverage */
        seeker.wrangleGuids(guids, wrangler);
        assertTrue(wrangled.get());
        assertTrue(seeker.wrangledGuid);
        assertTrue(seeker.isWrangling(guid));
        assertTrue(seeker.isWranglingWith(guid, wrangler));

        /*
         * If the GLFW joystick seeker is told to wrangle one
         * or more GUIDs with the same wrangler as previous,
         * then it should do nothing.
         */
        wrangled.set(false);
        seeker.wrangledGuid = false;
        seeker.wrangleGuids(guids, wrangler);
        assertFalse(wrangled.get());
        assertFalse(seeker.wrangledGuid);

        /*
         * A null value is allowed when setting a callback.
         * This should have the effect of removing the callback.
         */
        assertDoesNotThrow(() -> seeker.onWrangleGuid(null));

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(guid);

            /*
             * If a GLFW joystick wrangler returns a null device,
             * it has broken the promise it made to never return
             * a null value (if something went wrong, it should
             * have thrown an exception.) As such, the internal
             * seekImpl() method must throw an exception.
             */
            seeker.wrangleGuid(guid, (g, w) -> null);
            assertThrows(KetillException.class, seeker::seek);

            AtomicBoolean discovered = new AtomicBoolean();
            seeker.onDiscoverDevice((s, d) -> discovered.set(d == device));

            /*
             * Now that a valid wrangler has been registered, the
             * device should be discovered after a call to seek().
             */
            seeker.wrangleGuid(guid, wrangler);
            seeker.seek(); /* trigger device wrangling */
            assertTrue(discovered.get());

            AtomicBoolean released = new AtomicBoolean();
            seeker.onReleaseGuid((s, g, w) -> released.set(g.equals(guid)));

            AtomicBoolean forgot = new AtomicBoolean();
            seeker.onForgetDevice((s, d) -> forgot.set(d == device));

            /*
             * When the wrangler for a GUID is re-assigned, the
             * GUID in question must first be released before it
             * can be given a new wrangler. This is to ensure
             * the proper state handling of joysticks.
             */
            seeker.wrangleGuid(guid, (g, w) -> device);
            assertTrue(released.get());
            assertTrue(forgot.get());
            assertTrue(seeker.isWrangling(guid));
            assertFalse(seeker.isWranglingWith(guid, wrangler));
        }
    }

    @Test
    void releaseGuid() {
        /*
         * It would not make sense to release a null GUID.
         * As such, assume  this was a mistake by the user
         * and throw an exception.
         */
        Collection<String> nullGuids = Collections.singletonList(null);
        assertThrows(NullPointerException.class,
                () -> seeker.releaseGuid(null));
        assertThrows(NullPointerException.class,
                () -> seeker.releaseGuids(null));
        assertThrows(NullPointerException.class,
                () -> seeker.releaseGuids(nullGuids));

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(guid);

            /* connect device for next test */
            seeker.wrangleGuid(guid, wrangler);
            seeker.seek();

            AtomicBoolean released = new AtomicBoolean();
            seeker.onReleaseGuid((s, g, w) ->
                    released.set(g.equals(guid) && w == wrangler));

            AtomicBoolean forgot = new AtomicBoolean();
            seeker.onForgetDevice((s, d) -> forgot.set(d == device));

            /*
             * When a GUID is released, the seeker must forget all
             * currently connected joysticks with a matching GUID.
             * Failing to do so would result in unwanted joysticks
             * lingering until they disconnect themselves.
             */
            seeker.releaseGuid(guid);
            assertTrue(released.get());
            assertTrue(forgot.get());
            assertTrue(seeker.releasedGuid);
            assertFalse(seeker.isWrangling(guid));
            assertFalse(seeker.isWranglingWith(guid, wrangler));

            /*
             * If a GUID that is not being wrangled is released, then
             * the GLFW joystick seeker should do nothing.
             */
            released.set(false);
            seeker.releasedGuid = false;
            seeker.releaseGuid(guid);
            assertFalse(released.get());
            assertFalse(seeker.releasedGuid);
        }

        /*
         * A null value is allowed when setting a callback.
         * This should have the effect of removing the callback.
         */
        assertDoesNotThrow(() -> seeker.onReleaseGuid(null));
    }

    @Test
    void seekImpl() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            /* wrangle GUID for next tests */
            seeker.wrangleGuid(guid, wrangler);

            AtomicBoolean discovered = new AtomicBoolean();
            seeker.onDiscoverDevice((s, d) -> discovered.set(d == device));

            /*
             * When the GUID for a joystick is not equal to null,
             * that means it is currently connected. As such, it
             * should be wrangled and discovered as a device.
             */
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(guid);
            seeker.seek();
            assertTrue(discovered.get());

            AtomicBoolean forgot = new AtomicBoolean();
            seeker.onForgetDevice((s, d) -> forgot.set(d == device));

            /*
             * When the GUID for a joystick is equal to null,
             * that means it is no longer connected. As such,
             * it should be forgotten by the seeker.
             */
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(null);
            seeker.seek();
            assertTrue(forgot.get());

            /* discover device for next test */
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(guid);
            seeker.seek();
            forgot.set(false);

            /*
             * However, it is possible the joystick may indicate
             * that it is no longer connected (even though GLFW
             * says its GUID is not null). When this occurs, the
             * seeker should forget the device anyway.
             */
            when(device.isConnected()).thenReturn(false);
            seeker.seek();
            assertTrue(forgot.get());
        }
    }

}
