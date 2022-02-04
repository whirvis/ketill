package com.whirvis.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void aliases() {
        assertEquals(Direction.NORTH, Direction.UP);
        assertEquals(Direction.SOUTH, Direction.DOWN);
        assertEquals(Direction.WEST, Direction.LEFT);
        assertEquals(Direction.EAST, Direction.RIGHT);
    }

}