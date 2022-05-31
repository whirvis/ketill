package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class AnalogStickTest {

    private AnalogStick stick;

    @BeforeEach
    void createStick() {
        this.stick = new AnalogStick("stick");
    }

    @Test
    void testIsPressed() {
        Vector3f pos = new Vector3f();

        /*
         * It would not make sense to check if a null position was pointing
         * in a direction, or see if a position was pointing towards a null
         * direction. As such, assume these were mistakes by the user and
         * throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> AnalogStick.isPressed((Vector3fc) null, Direction.UP));
        assertThrows(NullPointerException.class,
                () -> AnalogStick.isPressed((StickPos) null, Direction.UP));
        assertThrows(NullPointerException.class,
                () -> AnalogStick.isPressed(pos, null));

        /* not facing any direction */
        assertFalse(AnalogStick.isPressed(pos, Direction.UP));
        assertFalse(AnalogStick.isPressed(pos, Direction.DOWN));
        assertFalse(AnalogStick.isPressed(pos, Direction.LEFT));
        assertFalse(AnalogStick.isPressed(pos, Direction.RIGHT));

        pos.y = 1.0F; /* facing upwards */
        assertTrue(AnalogStick.isPressed(pos, Direction.UP));
        assertFalse(AnalogStick.isPressed(pos, Direction.DOWN));

        pos.y = -1.0F; /* facing downwards */
        assertFalse(AnalogStick.isPressed(pos, Direction.UP));
        assertTrue(AnalogStick.isPressed(pos, Direction.DOWN));

        pos.x = -1.0F; /* facing left */
        assertTrue(AnalogStick.isPressed(pos, Direction.LEFT));
        assertFalse(AnalogStick.isPressed(pos, Direction.RIGHT));

        pos.x = 1.0F; /* facing right */
        assertFalse(AnalogStick.isPressed(pos, Direction.LEFT));
        assertTrue(AnalogStick.isPressed(pos, Direction.RIGHT));
    }

    @Test
    void testInit() {
        /*
         * It is legal to create an analog stick without a Z-button or a
         * base calibration. As such, these should not throw an exception.
         */
        assertDoesNotThrow(() -> new AnalogStick("stick",
                (ControllerButton) null));
        assertDoesNotThrow(() -> new AnalogStick("stick",
                (AnalogStickCalibration) null));
    }

    @Test
    void testGetDeviceType() {
        assertSame(Controller.class, stick.getDeviceType());
    }

    @Test
    void testGetZButton() {
        assertNull(stick.getZButton());
    }

    @Test
    void testGetBaseCalibration() {
        assertNull(stick.getBaseCalibration());
    }

    @Test
    void testGetState() {
        Controller controller = mock(Controller.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        StickPosZ internal = stick.getInternalState(observer);
        assertNotNull(internal);

        StickPos container = stick.getContainerState(internal);
        assertNotNull(container);
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(AnalogStick.class, stick);
    }

}
