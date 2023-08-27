package io.ketill;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class NoInternalsTest {

    @Test
    void ensureSingleton() {
        Class<?> clazz = NoInternals.class;
        assertTrue(Modifier.isFinal(clazz.getModifiers()),
                clazz.getName() + " must be final");
        assertEquals(0, clazz.getConstructors().length,
                clazz.getName() + " must be inconstructible");
    }

    @Test
    void ensureToStringImplemented() throws NoSuchMethodException {
        Class<?> clazz = NoInternals.class;
        assertEquals(clazz, clazz.getMethod("toString")
                .getDeclaringClass());
    }

}
