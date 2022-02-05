package com.whirvis.ketill;

public enum Direction {

    UP, DOWN, LEFT, RIGHT;

    /**
     * Aliases for {@link #UP}, {@link #DOWN}, {@link #LEFT}, and
     * {@link #RIGHT}.
     */
    /* @formatter:off */
    public static final Direction
            NORTH = UP, SOUTH = DOWN,
            WEST = LEFT, EAST = RIGHT;
    /* @formatter:on */

}
