package io.ketill.controller;

import io.ketill.Direction;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class AnalogStickTest {

    @Test
    void testIsPressed() {
        Vector3f pos = new Vector3f();

        /*
         * It would not make sense to check if an analog
         * stick is facing towards a null direction. As
         * such, assume this was a user mistake and throw
         * an exception.
         */
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

    private AnalogStick stick;

    @BeforeEach
    void createStick() {
        this.stick = new AnalogStick("stick");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class,
                () -> new AnalogStick(null));
    }

    @Test
    void ensureNullZButton() {
        assertNull(stick.zButton);
    }

    @Test
    void testGetState() {
        StickPosZ internal = stick.getInternalState();
        assertNotNull(internal);
        StickPos container = stick.getContainerState(internal);
        assertNotNull(container);
    }

}
