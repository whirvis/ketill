package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

final class KetillIoExceptionTests {

    private static String message;
    private static Throwable cause;

    @BeforeAll
    static void launch() {
        message = "message";
        cause = new Throwable();
    }

    @Test
    void classIsAbstract() {
        Class<?> clazz = KetillIoException.class;
        assertTrue(Modifier.isAbstract(clazz.getModifiers()),
                clazz.getName() + " must be abstract");
    }

    @Test
    void initBehavesAsExpected() {
        KetillIoException a = new KetillIoException(message, cause) {};
        assertEquals(message, a.getMessage());
        assertEquals(cause, a.getCause());

        KetillIoException b = new KetillIoException(message) {};
        assertEquals(message, b.getMessage());
        assertNull(b.getCause());

        KetillIoException c = new KetillIoException(cause) {};
        assertEquals(cause.getClass().getName(), c.getMessage());
        assertEquals(cause, c.getCause());

        KetillIoException d = new KetillIoException() {};
        assertNull(d.getMessage());
        assertNull(d.getCause());
    }

}
