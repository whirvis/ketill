package com.whirvis.kibasan;

/**
 * Represents a generic direction.
 * <p>
 * How a direction is used generally depends on the implementation. A common
 * example is for D-pad buttons to be mapped to their corresponding direction.
 * It is also common to have a method which returns if an analog stick is
 * pressed in a certain direction. However, it may have other uses.
 */
public enum Direction {

	UP, DOWN, LEFT, RIGHT;

	/* @formatter: off */
	public static final Direction
			NORTH = UP,
			SOUTH = DOWN,
			WEST = LEFT,
			EAST = RIGHT;
	/* @formatter: on */

}
