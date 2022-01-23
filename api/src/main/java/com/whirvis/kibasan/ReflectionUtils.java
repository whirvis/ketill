package com.whirvis.kibasan;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReflectionUtils {

    private ReflectionUtils() {
        throw new UnsupportedOperationException();
    }

    protected static @NotNull Set<Field> getAllFields(@NotNull Class<?> clazz) {
        Set<Field> fields = new HashSet<>();
        Collections.addAll(fields, clazz.getDeclaredFields());
        Collections.addAll(fields, clazz.getFields());
        return fields;
    }

}
