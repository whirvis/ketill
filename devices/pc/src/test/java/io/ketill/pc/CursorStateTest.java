package io.ketill.pc;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class CursorStateTest {

    private static final Random RANDOM = new Random();

    @Test
    void __init__() {
        assertThrows(NullPointerException.class,
                () -> new CursorState(null));
    }

    private CursorStateZ internal;
    private CursorState container;

    @BeforeEach
    void setup() {
        this.internal = new CursorStateZ();
        this.container = new CursorState(internal);
    }

    @Test
    void isVisible() {
        internal.visible = true;
        assertTrue(container.isVisible());
        internal.visible = false;
        assertFalse(container.isVisible());
    }

    @Test
    void setVisible() {
        container.setVisible(true);
        assertTrue(internal.visible);
        container.setVisible(false);
        assertFalse(internal.visible);
    }

    @Test
    void getPosition() {
        Vector2f currentPos = internal.currentPos;
        currentPos.x = RANDOM.nextFloat();
        currentPos.y = RANDOM.nextFloat();

        Vector2fc pos = container.getPosition();
        assertEquals(currentPos.x, pos.x());
        assertEquals(currentPos.y, pos.y());
    }

    @Test
    void getX() {
        Vector2f currentPos = internal.currentPos;
        currentPos.x = RANDOM.nextFloat();
        assertEquals(currentPos.x, container.getX());
    }

    @Test
    void getY() {
        Vector2f currentPos = internal.currentPos;
        currentPos.y = RANDOM.nextFloat();
        assertEquals(currentPos.y, container.getY());
    }

    @Test
    void setPosition() {
        assertThrows(NullPointerException.class,
                () -> container.setPosition(null));

        float x = RANDOM.nextFloat();
        float y = RANDOM.nextFloat();
        container.setPosition(x, y);

        /*
         * When setting the position of the mouse, it is the responsibility
         * of the adapter to update the current position. As a result, the
         * current position should be at a zero value.
         */
        Vector2fc pos = container.getPosition();
        assertEquals(0.0F, pos.x());
        assertEquals(0.0F, pos.y());

        assertNotNull(internal.requestedPos);
        assertEquals(x, internal.requestedPos.x());
        assertEquals(y, internal.requestedPos.y());
    }

}
