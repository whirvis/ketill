package com.whirvis.kibasan;

public enum Direction {

    UP, DOWN, LEFT, RIGHT;

    /**
     * Aliases for {@link #UP}, {@link #DOWN}, {@link #LEFT}, and
     * {@link #RIGHT}.
     */
    /* @formatter:off */
    @SuppressWarnings("unused")
    public static final Direction
            NORTH = UP, SOUTH = DOWN,
            WEST = LEFT, EAST = RIGHT;
    /* @formatter:on */

}
