package io.ketill;

import org.jetbrains.annotations.NotNull;

public enum Direction {

    UP(0), DOWN(1), LEFT(2), RIGHT(3);

    /**
     * Aliases for {@link #UP}, {@link #DOWN}, {@link #LEFT}, and
     * {@link #RIGHT}.
     */
    /* @formatter:off */
    public static final Direction
            NORTH = UP, SOUTH = DOWN,
            WEST = LEFT, EAST = RIGHT;
    /* @formatter:on */

    public final int id;

    Direction(int id) {
        this.id = id;
    }

    /**
     * @param id the event ID.
     * @return the {@code Direction} with the specified ID.
     * @throws IllegalArgumentException if no such direction exists.
     */
    public static @NotNull Direction fromId(int id) {
        for (Direction value : values()) {
            if (value.id == id) {
                return value;
            }
        }

        String msg = "no such ";
        msg += Direction.class.getSimpleName();
        msg += " with ID " + id;
        throw new IllegalArgumentException(msg);
    }

}
