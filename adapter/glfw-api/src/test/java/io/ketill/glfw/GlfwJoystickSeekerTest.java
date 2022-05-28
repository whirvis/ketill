package io.ketill.glfw;

import io.ketill.IoDeviceDiscoverEvent;
import io.ketill.IoDeviceForgetEvent;
import io.ketill.KetillException;
import io.ketill.controller.Controller;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GlfwJoystickSeekerTest {

    private static final Random RANDOM = new Random();

    private static long ptr_glfwWindow;
    private static int glfwJoystick;

    /*
     * For the next tests to successfully execute, GLFW must initialize
     * successfully. If it fails to do so, that is fine. It just means
     * the current machine does not have access to GLFW.
     */
    @BeforeAll
    static void initGlfw() {
        assumeTrue(glfwInit());
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        ptr_glfwWindow = glfwCreateWindow(1024, 768, "window", 0L, 0L);
        glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);
    }

    private Controller controller;
    private String guid;
    private GlfwJoystickWrangler<Controller> wrangler;
    private MockGlfwJoystickSeeker seeker;

    @BeforeEach
    void createSeeker() {
        this.controller = mock(Controller.class);
        this.guid = Integer.toHexString(RANDOM.nextInt());
        this.wrangler = (g, w) -> controller;
        this.seeker = new MockGlfwJoystickSeeker(ptr_glfwWindow);
    }

    @Test
    void testInit() {
        /*
         * For a GLFW joystick seeker to function, a valid window pointer must
         * be provided by the user. The GlfwJoystickSeeker class should make a
         * call to GlfwUtils.requireWindow(). Since a NULL pointer was passed
         * here, a NullPointerException should be thrown.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwJoystickSeeker(0x00));

        /*
         * It makes no sense for the device type of GLFW joystick seeker to
         * be a null class. Assume this was a mistake by the user and throw
         * an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwJoystickSeeker(null, ptr_glfwWindow));
    }

    @Test
    void testLoadJsonGuids() {
        /*
         * It would not make sense to try loading a JSON device GUIDs
         * container from a null path. Assume this was a mistake by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.loadJsonGuids(null));

        /*
         * There is no such filed named /io/ketill/glfw/missing.json
         * in the classpath. As such, this file should fail to load.
         */
        assertThrows(KetillException.class,
                () -> seeker.loadJsonGuids("missing.json"));

        /*
         * This file loads successfully, however there are no known systems
         * in this container. An exception must be thrown when this occurs,
         * as it means there won't be any GUIDs to wrangle for a joystick.
         * An absolute path is used here to ensure absolute paths work.
         */
        assertThrows(KetillException.class,
                () -> seeker.loadJsonGuids("alien_system.json"));

        /*
         * The test below ensures that both valid JSON GUIDs files load
         * successfully, and that using an absolute path is functional.
         */
        String absolutePath = "/io/ketill/glfw/valid.json";
        assertDoesNotThrow(() -> seeker.loadJsonGuids(absolutePath));
    }

    @Test
    void testIsWrangling() {
        /*
         * It would not make sense to check if a null GUID is currently being
         * wrangled. Assume this was user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isWrangling(null));

        /*
         * Since the GLFW joystick seeker has not been told to wrangle any
         * GUIDs yet, this should return false.
         */
        assertFalse(seeker.isWrangling(guid));
    }

    @Test
    void testGetWrangled() {
        /*
         * The getWrangled() method provides a read-only view of all
         * wrangled GUIDs in a GLFW joystick seeker. Ensure that it
         * never returns null (even when empty) and is unmodifiable.
         */
        Collection<String> wrangled = seeker.getWrangled();
        assertNotNull(wrangled); /* this should never be null, only empty */
        assertThrows(UnsupportedOperationException.class, wrangled::clear);

        /*
         * After assigning a GUID to a wrangler, it must be contained
         * in the collection returned by the getWrangled() method.
         */
        List<String> guids = Collections.singletonList(guid);
        seeker.wrangleGuids(guids, wrangler);
        assertIterableEquals(guids, seeker.getWrangled());
    }

    @Test
    void testIsWranglingWith() {
        /*
         * It would not make sense to check if a null GUID is currently being
         * wrangled. It also makes no sense to check if such a GUID is being
         * wrangled with a null wrangler. Assume these were mistakes by the
         * user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> seeker.isWranglingWith(null, wrangler));
        assertThrows(NullPointerException.class,
                () -> seeker.isWranglingWith(guid, null));

        /*
         * Since the GLFW joystick seeker has not been told to wrangle any
         * GUIDs yet, this should return false.
         */
        assertFalse(seeker.isWranglingWith(guid, wrangler));
    }

    @Test
    void testGetWrangler() {
        assertNull(seeker.getWrangler(guid));
        seeker.wrangleGuid(guid, wrangler);
        assertSame(wrangler, seeker.getWrangler(guid));
    }

    @Test
    void testWrangleGuid() {
        /*
         * It would not make sense to wrangle a null GUID or to use a null
         * wrangler. As a result, assume this was a mistake by the user and
         * throw an exception. However, empty GUID strings are allowed in
         * case of oddballs.
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
        seeker.subscribeEvents(WrangleGuidEvent.class,
                event -> wrangled.set(guid.equals(event.getGuid())));

        /* use wrangleGuids() here for full coverage */
        seeker.wrangleGuids(guids, wrangler);
        assertTrue(wrangled.get());
        assertTrue(seeker.wrangledGuid);
        assertTrue(seeker.isWrangling(guid));
        assertTrue(seeker.isWranglingWith(guid, wrangler));

        /*
         * If the GLFW joystick seeker is told to wrangle one or more GUIDs
         * with the same wrangler as previous, then it should do nothing.
         */
        wrangled.set(false);
        seeker.wrangledGuid = false;
        seeker.wrangleGuids(guids, wrangler);
        assertFalse(wrangled.get());
        assertFalse(seeker.wrangledGuid);

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(guid);

            /*
             * If a GLFW joystick wrangler returns a null device, it has
             * broken the promise to never return a null value (it should
             * have thrown an exception if something went wrong.) As such,
             * the internal seekImpl() method must throw an exception.
             */
            seeker.wrangleGuid(guid, (g, w) -> null);
            assertThrows(KetillException.class, seeker::seek);

            AtomicBoolean discovered = new AtomicBoolean();
            seeker.subscribeEvents(IoDeviceDiscoverEvent.class,
                    event -> discovered.set(event.getDevice() == controller));

            /*
             * Now that a valid wrangler has been registered, the device
             * should be discovered after a call to seek().
             */
            seeker.wrangleGuid(guid, wrangler);
            seeker.seek(); /* trigger device wrangling */
            assertTrue(discovered.get());

            AtomicBoolean released = new AtomicBoolean();
            seeker.subscribeEvents(ReleaseGuidEvent.class,
                    event -> released.set(guid.equals(event.getGuid())));

            AtomicBoolean forgot = new AtomicBoolean();
            seeker.subscribeEvents(IoDeviceForgetEvent.class,
                    event -> forgot.set(event.getDevice() == controller));

            /*
             * When the wrangler for a GUID is re-assigned, the GUID in
             * question must first be released before it is given a new
             * wrangler. This is to ensure the proper state handling of
             * joysticks.
             */
            seeker.wrangleGuid(guid, (g, w) -> controller);
            assertTrue(released.get());
            assertTrue(forgot.get());
            assertTrue(seeker.isWrangling(guid));
            assertFalse(seeker.isWranglingWith(guid, wrangler));
        }
    }

    @Test
    void testReleaseGuid() {
        /*
         * It would not make sense to release a null GUID. As such, assume
         * this was a mistake by the user and throw an exception.
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
            seeker.subscribeEvents(ReleaseGuidEvent.class,
                    event -> released.set(guid.equals(event.getGuid())));

            AtomicBoolean forgot = new AtomicBoolean();
            seeker.subscribeEvents(IoDeviceForgetEvent.class,
                    event -> forgot.set(event.getDevice() == controller));

            /*
             * When a GUID is released, the seeker must forget all currently
             * connected joysticks with a matching GUID. Not doing so would
             * result in unwanted joysticks lingering until they disconnect
             * by themselves.
             */
            seeker.releaseGuid(guid);
            assertTrue(released.get());
            assertTrue(forgot.get());
            assertTrue(seeker.releasedGuid);
            assertFalse(seeker.isWrangling(guid));
            assertFalse(seeker.isWranglingWith(guid, wrangler));

            /*
             * If a GUID that is not being wrangled is released, then the
             * GLFW joystick seeker should do nothing.
             */
            released.set(false);
            seeker.releasedGuid = false;
            seeker.releaseGuid(guid);
            assertFalse(released.get());
            assertFalse(seeker.releasedGuid);
        }
    }

    @Test
    void testSeekImpl() {
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            /*
             * It would not make sense to seek when no GUIDs are wrangled.
             * Assume this was a user mistake and throw an exception.
             */
            assertThrows(KetillException.class, seeker::seek);

            /* wrangle GUID for next tests */
            seeker.wrangleGuid(guid, wrangler);

            AtomicBoolean discovered = new AtomicBoolean();
            seeker.subscribeEvents(IoDeviceDiscoverEvent.class,
                    event -> discovered.set(event.getDevice() == controller));

            /*
             * When the GUID for a joystick is not equal to null, that means
             * it is currently connected. As such, it should be wrangled and
             * discovered as a device.
             */
            glfw.when(() -> glfwGetJoystickGUID(glfwJoystick))
                    .thenReturn(guid);
            seeker.seek();
            assertTrue(discovered.get());

            AtomicBoolean forgot = new AtomicBoolean();
            seeker.subscribeEvents(IoDeviceForgetEvent.class,
                    event -> forgot.set(event.getDevice() == controller));

            /*
             * When the GUID for a joystick is equal to null, that means
             * it is no longer connected. As such, it should be forgotten
             * by the seeker.
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
             * However, it is possible the joystick may indicate that it
             * is no longer connected (even though GLFW says its GUID is
             * not null). When this occurs, the seeker should forget the
             * device anyway.
             */
            when(controller.isConnected()).thenReturn(false);
            seeker.seek();
            assertTrue(forgot.get());
        }
    }

    @AfterAll
    static void terminateGlfw() {
        glfwDestroyWindow(ptr_glfwWindow);
        glfwTerminate();
    }

}
