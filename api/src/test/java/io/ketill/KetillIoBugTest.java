package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public final class KetillIoBugTest {

    private static String message;
    private static Throwable cause;

    @BeforeAll
    static void setup() {
        message = "message";
        cause = new Throwable();
    }

    @Test
    void testInit() {
        KetillIoBug a = new KetillIoBug(message, cause);
        assertEquals(message, a.getMessage());
        assertEquals(cause, a.getCause());

        KetillIoBug b = new KetillIoBug(message);
        assertEquals(message, b.getMessage());
        assertNull(b.getCause());

        KetillIoBug c = new KetillIoBug(cause);
        assertEquals(cause.getClass().getName(), c.getMessage());
        assertEquals(cause, c.getCause());

        KetillIoBug d = new KetillIoBug();
        assertNull(d.getMessage());
        assertNull(d.getCause());
    }

}
