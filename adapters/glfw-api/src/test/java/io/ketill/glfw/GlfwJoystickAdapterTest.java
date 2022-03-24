package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static io.ketill.glfw.MockGlfw.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class GlfwJoystickAdapterTest {

    private static final Random RANDOM = new Random();

    private long ptr_glfwWindow;
    private int glfwJoystick;
    private ByteBuffer buttons;
    private FloatBuffer axes;
    private MockJoystick joystick;
    private MockGlfwJoystickAdapter adapter;

    @BeforeAll
    static void __init__() {
        IoDevice device = mock(IoDevice.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);

        /*
         * For a GLFW joystick adapter to function, a valid
         * window pointer must be provided. As such, throw an
         * exception if the pointer is NULL or does not point
         * to a valid GLFW window.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwJoystickAdapter(device, registry,
                        0x00, 0));
        assertThrows(IllegalArgumentException.class,
                () -> new MockGlfwJoystickAdapter(device, registry,
                        0x01, 0));

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mockGlfwWindow(glfw, 0x01);

            /*
             * For a GLFW joystick adapter to function, a valid
             * joystick must be provided. Throw an exception if
             * the joystick is not within bounds.
             */
            assertThrows(IllegalArgumentException.class,
                    () -> new MockGlfwJoystickAdapter(device, registry,
                            0x01, GLFW_JOYSTICK_LAST + 1));
            assertThrows(IllegalArgumentException.class,
                    () -> new MockGlfwJoystickAdapter(device, registry,
                            0x01, GLFW_JOYSTICK_1 - 1));
        }
    }

    @BeforeEach
    void setup() {
        /*
         * Any valid, randomly chosen pointer and GLFW joystick
         * should suffice for the following tests. The randomly
         * chosen pointer will be mocked so a mock GLFW adapter
         * can be created.
         */
        this.ptr_glfwWindow = RANDOM.nextLong();
        this.glfwJoystick = RANDOM.nextInt(GLFW_JOYSTICK_LAST + 1);

        this.buttons = ByteBuffer.allocate(16);
        this.axes = FloatBuffer.allocate(4);

        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            mockGlfwWindow(glfw, ptr_glfwWindow);

            AtomicReference<MockGlfwJoystickAdapter> adapter =
                    new AtomicReference<>();
            AdapterSupplier<IoDevice> adapterSupplier = (c, r) -> {
                adapter.set(new MockGlfwJoystickAdapter(c, r,
                        ptr_glfwWindow, glfwJoystick));
                return adapter.get();
            };

            this.joystick = new MockJoystick(adapterSupplier);
            this.adapter = adapter.get();
        }
    }

    @Test
    void mapButton() {
        /*
         * It would not make sense to map a null button or for a
         * button to be mapped to a negative index. Assume these
         * were a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapButton(null, 0));
        assertThrows(IllegalArgumentException.class,
                () -> adapter.mapButton(MockJoystick.BUTTON, -1));

        /* map random button for next test */
        int glfwButton = RANDOM.nextInt(buttons.limit());
        adapter.mapButton(MockJoystick.BUTTON, glfwButton);

        /*
         * Now that a randomly chosen button has been mapped,
         * poll the device to ensure that it properly updates
         * this button with its assigned adapter.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickButtons(glfwJoystick))
                    .thenReturn(buttons);

            buttons.put(glfwButton, (byte) GLFW_PRESS);
            joystick.poll(); /* update buttons */
            assertTrue(joystick.button.isPressed());

            buttons.put(glfwButton, (byte) GLFW_RELEASE);
            joystick.poll(); /* update buttons */
            assertFalse(joystick.button.isPressed());
        }
    }

    @Test
    void mapStick() {
        /*
         * It would not make sense to map a null stick or for a
         * stick to be given a null mapping. Assume these were a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapStick(null, new GlfwStickMapping(0, 0)));
        assertThrows(NullPointerException.class,
                () -> adapter.mapStick(MockJoystick.STICK, null));

        /* map pre-determined stick for next test */
        int glfwXAxis = 0, glfwYAxis = 1;
        GlfwStickMapping mapping = new GlfwStickMapping(glfwXAxis, glfwYAxis);
        adapter.mapStick(MockJoystick.STICK, mapping);

        /*
         * Now that a pre-determined stick has been mapped,
         * poll the device to ensure that it properly updates
         * this stick with its assigned adapter.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);

            axes.put(glfwXAxis, 1.23F).put(glfwYAxis, 4.56F);
            joystick.poll(); /* update axes */
            assertEquals(1.23F, joystick.stick.x());
            assertEquals(4.56F, joystick.stick.y());

            axes.put(glfwXAxis, 0.00F).put(glfwYAxis, 0.00F);
            joystick.poll(); /* update axes */
            assertEquals(0.00F, joystick.stick.x());
            assertEquals(0.00F, joystick.stick.y());
        }

        /* remap stick now with Z-button for next test */
        int glfwZButton = 2;
        mapping = new GlfwStickMapping(glfwXAxis, glfwYAxis, glfwZButton);
        adapter.mapStick(MockJoystick.STICK, mapping);

        /*
         * Now that a stick with a Z-button has been mapped,
         * poll the device to ensure that it properly updates
         * this stick with its assigned adapter.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickButtons(glfwJoystick))
                    .thenReturn(buttons);

            buttons.put(glfwZButton, (byte) GLFW_PRESS);
            joystick.poll(); /* update axes */
            assertEquals(-1.0F, joystick.stick.z());

            buttons.put(glfwZButton, (byte) GLFW_RELEASE);
            joystick.poll(); /* update axes */
            assertEquals(0.0F, joystick.stick.z());
        }
    }

    @Test
    void mapTrigger() {
        /*
         * It would not make sense to map a null trigger or for a
         * trigger to be mapped to a negative index. Assume these
         * were a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> adapter.mapTrigger(null, 0));
        assertThrows(IllegalArgumentException.class,
                () -> adapter.mapTrigger(MockJoystick.TRIGGER, -1));

        /* map random trigger for next test */
        int glfwAxis = RANDOM.nextInt(axes.limit());
        adapter.mapTrigger(MockJoystick.TRIGGER, glfwAxis);

        /*
         * Now that a randomly chosen trigger has been mapped,
         * poll the device to ensure that it properly updates
         * this trigger with its assigned adapter.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);

            axes.put(glfwAxis, 1.23F);
            joystick.poll(); /* update axes */
            assertEquals(1.23F, joystick.trigger.force());

            axes.put(glfwAxis, 0.00F);
            joystick.poll(); /* update axes */
            assertEquals(0.00F, joystick.trigger.force());
        }
    }

    @Test
    void getButtonCount() {
        /*
         * Since the internal buttons buffer has yet to be set,
         * the device adapter must return -1. This  prevents an
         * exception from being thrown, and lets the user know
         * the device has yet to be polled.
         */
        assertEquals(-1, adapter.getButtonCount());

        /*
         * After polling the device, the getButtonCount() method
         * must return a value equal to the limit of the buttons
         * buffer that has been supplied to it.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickButtons(glfwJoystick))
                    .thenReturn(buttons);
            adapter.pollDevice();
            assertEquals(buttons.limit(), adapter.getButtonCount());
        }
    }

    @Test
    void isPressed() {
        /*
         * It would not make sense to get the value of a button
         * with a negative index. Assume this was a mistake by
         * the user and throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.isPressed(-1));

        /*
         * If the internal buttons buffer has yet to be set, return
         * a value of 0.0F. This will correct itself to a current
         * value after the first poll. This is to prevent the user
         * from having to check the current state of the adapter.
         */
        assertFalse(adapter.isPressed(0));

        /* update state of random button for next test */
        int glfwButton = RANDOM.nextInt(buttons.limit());
        buttons.put(glfwButton, (byte) GLFW_PRESS);

        /*
         * The adapter must first be polled for it to update the
         * internal buttons to the updated state. If the state is
         * not correct after polling, something has gone wrong.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickButtons(glfwJoystick))
                    .thenReturn(buttons);
            adapter.pollDevice(); /* update buttons */
            assertTrue(adapter.isPressed(glfwButton));
        }

        /*
         * Since the device has been polled (meaning the buttons
         * buffer has been set), the adapter can check if a given
         * index is out of bounds for the joystick's axes. When
         * this occurs, assume it was a mistake by the user and
         * throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.isPressed(buttons.limit()));
    }

    @Test
    void getAxisCount() {
        /*
         * Since the internal axes buffer has yet to be set, the
         * device adapter must return -1. This is to prevent an
         * exception from being thrown, and lets the user know
         * the device has yet to be polled.
         */
        assertEquals(-1, adapter.getAxisCount());

        /*
         * After polling the device, the getAxisCount() method
         * must return a value equal to the limit of the axes
         * buffer that has been supplied to it.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);
            adapter.pollDevice();
            assertEquals(axes.limit(), adapter.getAxisCount());
        }
    }

    @Test
    void getAxis() {
        /*
         * It would not make sense to get the value of an axis
         * with a negative index. Assume this was a mistake by
         * the user and throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.getAxis(-1));

        /*
         * If the internal axes buffer has yet to be set, return
         * a value of 0.0F. This will correct itself to a current
         * value after the first poll. This is to prevent the user
         * from having to check the current state of the adapter.
         */
        assertEquals(0.0F, adapter.getAxis(0));

        /* update value of random axis for next test */
        int glfwAxis = RANDOM.nextInt(axes.limit());
        axes.put(glfwAxis, 1.23F);

        /*
         * The adapter must first be polled for it to update the
         * internal axes to the updated value. If the value is
         * not correct after polling, something has gone wrong.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwGetJoystickAxes(glfwJoystick))
                    .thenReturn(axes);
            adapter.pollDevice(); /* update axes */
            assertEquals(1.23F, adapter.getAxis(glfwAxis));
        }

        /*
         * Since the device has been polled (meaning the axes
         * buffer has been set), the adapter can check if an
         * index is out of bounds for the joystick's axes. When
         * this occurs, assume it was a mistake by the user and
         * throw an exception.
         */
        assertThrows(IndexOutOfBoundsException.class,
                () -> adapter.getAxis(axes.limit()));
    }

    @Test
    void pollDevice() {
        /*
         * When polled, the GLFW joystick adapter is expected to
         * update its internal buttons and axes by fetching their
         * current state from GLFW.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            adapter.pollDevice();
            glfw.verify(() -> glfwGetJoystickButtons(glfwJoystick));
            glfw.verify(() -> glfwGetJoystickAxes(glfwJoystick));
        }
    }

    @Test
    void isDeviceConnected() {
        /*
         * When checking if the joystick is connected, the adapter
         * is expected to ask GLFW directly on each call. This is
         * ensured by verifying the adapter returns true and then
         * returns false, in that order, using mocking.
         */
        try (MockedStatic<GLFW> glfw = mockStatic(GLFW.class)) {
            glfw.when(() -> glfwJoystickPresent(glfwJoystick))
                    .thenReturn(true);
            assertTrue(adapter.isDeviceConnected());

            glfw.when(() -> glfwJoystickPresent(glfwJoystick))
                    .thenReturn(false);
            assertFalse(adapter.isDeviceConnected());
        }
    }

}
