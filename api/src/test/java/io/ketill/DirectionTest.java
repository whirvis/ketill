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

}