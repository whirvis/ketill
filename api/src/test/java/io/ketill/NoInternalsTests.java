package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

final class NoInternalsTests {

    private static Class<?> clazz;
    private static Method toStringMethod;
    private static Field instanceField;

    @BeforeAll
    static void launch() throws NoSuchMethodException, NoSuchFieldException {
        clazz = NoInternals.class;
        toStringMethod = clazz.getDeclaredMethod("toString");
        instanceField = clazz.getDeclaredField("INSTANCE");
    }

    @Test
    void classIsSingleton() {
        assertTrue(Modifier.isFinal(clazz.getModifiers()),
                clazz.getName() + " must be final");
        assertEquals(0, clazz.getConstructors().length,
                clazz.getName() + " must be inconstructible");

        assertTrue(Modifier.isStatic(instanceField.getModifiers()),
                instanceField.getName() + " must be static");
        assertTrue(Modifier.isFinal(instanceField.getModifiers()),
                instanceField.getName() + " must be final");
    }

    @Test
    void toStringIsOverridden() {
        assertEquals(clazz, toStringMethod.getDeclaringClass(),
                toStringMethod.getName() + " must be overridden");
        assertNotNull(NoInternals.INSTANCE.toString());
    }

}
