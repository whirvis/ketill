package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class CursorTest {

    private Cursor cursor;

    @BeforeEach
    void createCursor() {
        this.cursor = new Cursor("cursor");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class, () -> new Cursor(null));
    }

    @Test
    void testGetState() {
        CursorStateZ internal = cursor.getInternalState();
        assertNotNull(internal);
        CursorState container = cursor.getContainerState(internal);
        assertNotNull(container);
    }

}
