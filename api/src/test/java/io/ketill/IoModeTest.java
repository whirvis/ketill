package io.ketill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public final class IoModeTest {

    @Test
    void testIsRead() {
        assertTrue(IoMode.READ.isRead());
        assertFalse(IoMode.WRITE.isRead());
        assertTrue(IoMode.READ_WRITE.isRead());
    }

    @Test
    void testIsWrite() {
        assertFalse(IoMode.READ.isWrite());
        assertTrue(IoMode.WRITE.isWrite());
        assertTrue(IoMode.READ_WRITE.isWrite());
    }

    @Test
    void testSupports() {
        for (IoMode mode : IoMode.values()) {
            assertFalse(mode.supports(null));
        }

        assertTrue(IoMode.READ.supports(IoMode.READ));
        assertFalse(IoMode.READ.supports(IoMode.WRITE));
        assertFalse(IoMode.READ.supports(IoMode.READ_WRITE));

        assertFalse(IoMode.WRITE.supports(IoMode.READ));
        assertTrue(IoMode.WRITE.supports(IoMode.WRITE));
        assertFalse(IoMode.WRITE.supports(IoMode.READ_WRITE));

        assertTrue(IoMode.READ_WRITE.supports(IoMode.READ));
        assertTrue(IoMode.READ_WRITE.supports(IoMode.WRITE));
        assertTrue(IoMode.READ_WRITE.supports(IoMode.READ_WRITE));
    }

    @Test
    void testIds() {
        assertArrayEquals(
                new String[]{"r", "rb"},
                IoMode.READ.ids());
        assertArrayEquals(
                new String[]{"w", "wb", "a", "ab"},
                IoMode.WRITE.ids());
        assertArrayEquals(
                new String[]{"r+", "rb+", "w+", "wb+", "a+", "ab+"},
                IoMode.READ_WRITE.ids());
    }

    @Test
    void testOf() {
        assertThrows(NullPointerException.class,
                () -> IoMode.of(null));
        assertThrows(IllegalArgumentException.class,
                () -> IoMode.of("rw"));

        assertSame(IoMode.READ, IoMode.of("r"));
        assertSame(IoMode.READ, IoMode.of("rb"));

        assertSame(IoMode.WRITE, IoMode.of("w"));
        assertSame(IoMode.WRITE, IoMode.of("wb"));
        assertSame(IoMode.WRITE, IoMode.of("a"));
        assertSame(IoMode.WRITE, IoMode.of("ab"));

        assertSame(IoMode.READ_WRITE, IoMode.of("r+"));
        assertSame(IoMode.READ_WRITE, IoMode.of("rb+"));
        assertSame(IoMode.READ_WRITE, IoMode.of("w+"));
        assertSame(IoMode.READ_WRITE, IoMode.of("wb+"));
        assertSame(IoMode.READ_WRITE, IoMode.of("a+"));
        assertSame(IoMode.READ_WRITE, IoMode.of("ab+"));
    }

}
