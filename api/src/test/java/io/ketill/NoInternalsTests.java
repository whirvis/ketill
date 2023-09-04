package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

public final class NoInternalsTest {

    private static Class<?> clazz;

    @BeforeAll
    static void launch() {
        clazz = NoInternals.class;
    }

    @Test
    void ensureSingleton() {
        assertTrue(Modifier.isFinal(clazz.getModifiers()),
                clazz.getName() + " must be final");
        assertEquals(0, clazz.getConstructors().length,
                clazz.getName() + " must be inconstructible");
    }

    @Test
    void ensureToStringImplemented() throws NoSuchMethodException {
        Method toString = clazz.getMethod("toString");
        assertEquals(clazz, toString.getDeclaringClass());
    }

    @Test
    void testToString() {
        assertNotNull(NoInternals.INSTANCE.toString());
    }

}
