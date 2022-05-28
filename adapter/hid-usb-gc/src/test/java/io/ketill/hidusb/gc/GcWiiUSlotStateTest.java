package io.ketill.hidusb.gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
class GcWiiUSlotStateTest {

    private GcWiiUSlotState slot;

    @BeforeEach
    void createState() {
        this.slot = new GcWiiUSlotState();
    }

    @Test
    void testIsConnected() {
        slot.data[0] = (byte) 0x10;
        slot.poll(); /* update type field */
        assertTrue(slot.isConnected());

        slot.data[0] = (byte) 0x00;
        slot.poll(); /* update type field */
        assertFalse(slot.isConnected());
    }

    @Test
    void testIsButtonPressed() {
        int buttonCount = GcWiiUSlotState.BUTTON_COUNT;
        int fromIndex = 1, toIndex = fromIndex + 2;

        assertThrows(IndexOutOfBoundsException.class,
                () -> slot.isButtonPressed(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> slot.isButtonPressed(buttonCount));

        /*
         * For the next test, set the bytes for each button to 0xFF. This
         * will flip every button bit to on, indicating they are pressed.
         * Afterwards, go through each button and ensure that isPressed()
         * returns true.
         */
        Arrays.fill(slot.data, fromIndex, toIndex, (byte) 0xFF);
        slot.poll(); /* update buttons field */
        for (int i = 0; i < buttonCount; i++) {
            assertTrue(slot.isButtonPressed(i));
        }

        /*
         * For the next test, set the bytes for each button to 0x00. This
         * will flip every button bit to off, indicating they are released.
         * Afterwards, go through each button and ensure that isPressed()
         * returns false.
         */
        Arrays.fill(slot.data, fromIndex, toIndex, (byte) 0x00);
        slot.poll(); /* update buttons field */
        for (int i = 0; i < buttonCount; i++) {
            assertFalse(slot.isButtonPressed(i));
        }
    }

    @Test
    void testGetAnalogAxis() {
        int axisCount = GcWiiUSlotState.ANALOG_COUNT;
        int fromIndex = 3, toIndex = fromIndex + axisCount;

        assertThrows(IndexOutOfBoundsException.class,
                () -> slot.getAnalogAxis(-1));
        assertThrows(IndexOutOfBoundsException.class,
                () -> slot.getAnalogAxis(axisCount));

        /*
         * For the next test, set the value for each analog axis to 0xFF.
         * Afterwards, go through each axis and ensure that getAnalogAxis()
         * returns a value of 0xFF.
         */
        Arrays.fill(slot.data, fromIndex, toIndex, (byte) 0xFF);
        slot.poll(); /* update axes field */
        for (int i = 0; i < axisCount; i++) {
            assertEquals(0xFF, slot.getAnalogAxis(i));
        }

        /*
         * For the next test, set the value for each analog axis to 0x00.
         * Afterwards, go through each axis and ensure that getAnalogAxis()
         * returns a value of 0x00.
         */
        Arrays.fill(slot.data, fromIndex, toIndex, (byte) 0x00);
        slot.poll(); /* update axes field */
        for (int i = 0; i < axisCount; i++) {
            assertEquals(0x00, slot.getAnalogAxis(i));
        }
    }

    @Test
    void testIsRumbling() {
        assertFalse(slot.isRumbling());
    }

    @Test
    void testSetRumbling() {
        slot.setRumbling(true);
        assertTrue(slot.isRumbling());
        slot.setRumbling(false);
        assertFalse(slot.isRumbling());
    }

    @Test
    void testPoll() {
        /*
         * Other tests like testIsButtonPressed() and testGetAnalogAxis()
         * verify that this method works properly as a side effect. As a
         * result, there isn't really much to test here.
         */
        assertDoesNotThrow(() -> slot.poll());
    }

    @Test
    void testReset() {
        /* fill slot data for next test */
        Arrays.fill(slot.data, (byte) 0xFF);
        slot.setRumbling(true);

        slot.reset();

        /*
         * After the slot state has been reset, the slot data should only
         * contain values of zero. It should also have disabled rumbling
         * for the controller connected to it.
         */
        byte[] zero = new byte[slot.data.length];
        assertArrayEquals(zero, slot.data);
        assertFalse(slot.isRumbling());
    }

}
