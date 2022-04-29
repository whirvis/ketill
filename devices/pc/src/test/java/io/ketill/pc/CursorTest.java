package io.ketill.pc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class CursorTest {

    @Test
    void __init__() {
        assertThrows(NullPointerException.class, () -> new Cursor(null));
    }

    private Cursor cursor;

    @BeforeEach
    void setup() {
        this.cursor = new Cursor("cursor");
    }

    @Test
    void getState() {
        CursorStateZ internal = cursor.getInternalState();
        assertNotNull(internal);
        CursorState container = cursor.getContainerState(internal);
        assertNotNull(container);
    }

}
