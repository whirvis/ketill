package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class CursorTest {

    private MouseCursor cursor;

    @BeforeEach
    void createCursor() {
        this.cursor = new MouseCursor("cursor");
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class, () -> new MouseCursor(null));
    }

    @Test
    void testGetState() {
        CursorStateZ internal = cursor.getInternalState(null);
        assertNotNull(internal);
        CursorState container = cursor.getContainerState(internal);
        assertNotNull(container);
    }

}
