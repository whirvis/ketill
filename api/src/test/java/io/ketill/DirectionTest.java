package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void aliases() {
        assertSame(Direction.NORTH, Direction.UP);
        assertSame(Direction.SOUTH, Direction.DOWN);
        assertSame(Direction.WEST, Direction.LEFT);
        assertSame(Direction.EAST, Direction.RIGHT);
    }

    @Test
    void fromId() {
        /*
         * It would not make sense to get a direction from
         * an ID for a direction that does not exist. Assume
         * this was a user mistake by the user and throw an
         * exception.
         */
        assertThrows(IllegalArgumentException.class,
                () -> Direction.fromId(-1));

        /*
         * It would not make sense for fromId() to return a
         * different Direction than the one requested. This
         * is more or less a sanity check.
         */
        for (Direction value : Direction.values()) {
            assertSame(value, Direction.fromId(value.id));
        }
    }

}