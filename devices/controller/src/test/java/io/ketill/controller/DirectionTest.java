package io.ketill.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void ensureAccurateAliases() {
        assertSame(Direction.NORTH, Direction.UP);
        assertSame(Direction.SOUTH, Direction.DOWN);
        assertSame(Direction.WEST, Direction.LEFT);
        assertSame(Direction.EAST, Direction.RIGHT);
    }

    @Test
    void testGetId() {
        assertEquals(0, Direction.UP.getId());
        assertEquals(1, Direction.DOWN.getId());
        assertEquals(2, Direction.LEFT.getId());
        assertEquals(3, Direction.RIGHT.getId());
    }

    @Test
    void testFromId() {
        assertThrows(IllegalArgumentException.class,
                () -> Direction.fromId(-1));
        for (Direction value : Direction.values()) {
            assertSame(value, Direction.fromId(value.getId()));
        }
    }

}