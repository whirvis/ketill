package io.ketill;

import org.jetbrains.annotations.NotNull;

/**
 * Representations of the cardinal directions {@link #NORTH}, {@link #EAST},
 * {@link #SOUTH}, and {@link #WEST}. These are for use with two-dimensional
 * coordinate planes.
 */
public enum IoCardinal {

    /**
     * The direction facing north.<br>
     * This is the formal term for {@link #UP}.
     */
    NORTH(0),

    /**
     * The direction facing to the east.<br>
     * This is the formal term for {@link #RIGHT}.
     */
    EAST(1),

    /**
     * The direction facing south.<br>
     * This is the formal term for {@link #DOWN}.
     */
    SOUTH(2),

    /**
     * The direction facing to the west.<br>
     * This is the formal term for {@link #LEFT}.
     */
    WEST(3);

    /**
     * The direction facing upward.<br>
     * This is the casual term for {@link #NORTH}.
     */
    public static final IoCardinal UP = NORTH;

    /**
     * The direction facing to the right.<br>
     * This is the casual term for {@link #EAST}.
     */
    public static final IoCardinal RIGHT = EAST;

    /**
     * The direction facing downward.<br>
     * This is the casual term for {@link #SOUTH}.
     */
    public static final IoCardinal DOWN = SOUTH;

    /**
     * The direction facing to the left.<br>
     * This is the casual term for {@link #WEST}.
     */
    public static final IoCardinal LEFT = WEST;

    private final int id;

    IoCardinal(int id) {
        this.id = id;
    }

    /**
     * Returns the cardinal ID.
     *
     * @return the cardinal ID.
     * @see #fromId(int)
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns an {@code IoCardinal} from its ID.
     *
     * @param id the cardinal ID.
     * @return the cardinal with the specified ID.
     * @throws IllegalArgumentException if no such cardinal exists.
     */
    public static @NotNull IoCardinal fromId(int id) {
        for (IoCardinal value : values()) {
            if (value.id == id) {
                return value;
            }
        }
        throw new IllegalArgumentException("no such cardinal with ID " + id);
    }

}