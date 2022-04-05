package io.ketill.pressable;

import org.jetbrains.annotations.NotNull;

public enum PressableFeatureEventType {

    PRESS(0), HOLD(1), RELEASE(2);

    public final int id;

    PressableFeatureEventType(int id) {
        this.id = id;
    }

    /**
     * @param id the event ID.
     * @return the {@code PressableEventType} with the specified ID.
     * @throws IllegalArgumentException if no such event exists.
     */
    public static @NotNull PressableFeatureEventType fromId(int id) {
        for (PressableFeatureEventType value : values()) {
            if (value.id == id) {
                return value;
            }
        }

        String msg = "no such ";
        msg += PressableFeatureEventType.class.getSimpleName();
        msg += " with ID " + id;
        throw new IllegalArgumentException(msg);
    }

}
