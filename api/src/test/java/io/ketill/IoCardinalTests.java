package io.ketill;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

final class IoCardinalTests {

    @Test
    void aliasesMatch() {
        assertSame(IoCardinal.UP, IoCardinal.NORTH);
        assertSame(IoCardinal.RIGHT, IoCardinal.EAST);
        assertSame(IoCardinal.DOWN, IoCardinal.SOUTH);
        assertSame(IoCardinal.LEFT, IoCardinal.WEST);
    }

    @Test
    void idsAreUniqueAndConsistent() {
        Set<Integer> ids = new HashSet<>();
        for (IoCardinal value : IoCardinal.values()) {
            int id = value.getId();
            assertTrue(ids.add(id), "IDs must be unique");
            assertSame(value, IoCardinal.fromId(id));
        }
    }

    @Test
    void baselessIdsCauseException() {
        assertThrows(IllegalArgumentException.class,
                () -> IoCardinal.fromId(Integer.MAX_VALUE));
        assertThrows(IllegalArgumentException.class,
                () -> IoCardinal.fromId(Integer.MIN_VALUE));
    }

}
