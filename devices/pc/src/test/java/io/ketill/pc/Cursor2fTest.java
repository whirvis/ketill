package io.ketill.pc;

import org.joml.Vector2fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class Cursor2fTest {

    private Cursor2f cursor;

    @BeforeEach
    void setup() {
        this.cursor = new Cursor2f();
    }

    @Test
    void setPosition() {
        /*
         * It would not make sense to set the position of the
         * cursor to a null value. As such, assume this was a
         * mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> cursor.setPosition(null));

        /* set cursor to random position for next test */
        Random random = new Random();
        float xPos = random.nextFloat();
        float yPos = random.nextFloat();
        cursor.setPosition(xPos, yPos);

        /*
         * Ensure the last requested position is returned by this
         * method. It is used by adapters to  determine where the
         * cursor should be moved when requested via setPosition().
         */
        Vector2fc requested = cursor.getRequestedPos();
        assertEquals(xPos, requested.x());
        assertEquals(yPos, requested.y());

        /*
         * After getting the last requested position, this method
         * should return null until the user requests to change
         * the cursor position again. This provides adapters with
         * an easy to check if the cursor should be moved or not.
         */
        assertNull(cursor.getRequestedPos());
    }

}
