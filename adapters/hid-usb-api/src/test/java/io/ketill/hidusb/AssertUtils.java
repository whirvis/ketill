package io.ketill.hidusb;

import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class AssertUtils {

    static void assertThrowsCause(Throwable cause, Executable executable) {
        Throwable caught = null;
        try {
            executable.execute();
        } catch (Throwable throwable) {
            caught = throwable.getCause();
        }
        assertNotNull(caught);
        assertEquals(cause, caught);
    }

}
