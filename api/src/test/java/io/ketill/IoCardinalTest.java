package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class IoCardinalTest {

    @Test
    void testAliases() {
        assertEquals(IoCardinal.UP, IoCardinal.NORTH);
        assertEquals(IoCardinal.RIGHT, IoCardinal.EAST);
        assertEquals(IoCardinal.DOWN, IoCardinal.SOUTH);
        assertEquals(IoCardinal.LEFT, IoCardinal.WEST);
    }

    @Test
    void testGetId() {
        assertEquals(0, IoCardinal.NORTH.getId());
        assertEquals(1, IoCardinal.EAST.getId());
        assertEquals(2, IoCardinal.SOUTH.getId());
        assertEquals(3, IoCardinal.WEST.getId());
    }

    @Test
    void testFromId() {
        assertEquals(IoCardinal.NORTH, IoCardinal.fromId(0));
        assertEquals(IoCardinal.EAST, IoCardinal.fromId(1));
        assertEquals(IoCardinal.SOUTH, IoCardinal.fromId(2));
        assertEquals(IoCardinal.WEST, IoCardinal.fromId(3));

        assertThrows(IllegalArgumentException.class,
                () -> IoCardinal.fromId(-1));
    }

}
