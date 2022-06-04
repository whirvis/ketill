package io.ketill.controller;

import org.jetbrains.annotations.NotNull;

/**
 * Representations of the basic directions {@link #UP}, {@link #DOWN},
 * {@link #LEFT}, and {@link #RIGHT}. These values are used by both
 * {@link ControllerButton} and {@link AnalogStick}.
 */
public enum Direction {

    /**
     * The direction facing upward.<br>
     * This is layman's terms for {@link #NORTH}.
     */
    UP(0),

    /**
     * The direction facing downward.<br>
     * This is layman's terms for {@link #SOUTH}.
     */
    DOWN(1),

    /**
     * The direction facing to the left.<br>
     * This is layman's terms for {@link #WEST}.
     */
    LEFT(2),

    /**
     * The direction facing to the right.<br>
     * This is layman's terms for {@link #EAST}.
     */
    RIGHT(3);

    /**
     * Cardinal aliases for {@link #UP}, {@link #DOWN}, {@link #LEFT}, and
     * {@link #RIGHT}.
     */
    /* @formatter:off */
    public static final Direction
            NORTH = UP, SOUTH = DOWN,
            WEST = LEFT, EAST = RIGHT;
    /* @formatter:on */

    private final int id;

    Direction(int id) {
        this.id = id;
    }

    /**
     * Returns the direction ID.
     *
     * @return the direction ID.
     * @see #fromId(int)
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns a {@code Direction} from its ID.
     *
     * @param id the direction ID.
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
