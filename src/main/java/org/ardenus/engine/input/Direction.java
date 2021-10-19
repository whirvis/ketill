package org.ardenus.engine.input;

import org.ardenus.engine.util.EnumAlias;

/**
 * Represents a generic direction.
 * <p>
 * How a direction is used is generally dependant on the implementation. A
 * common example is for D-pad buttons to be mapped to their corresponding
 * direction. It is also common to have a method which returns if an analog
 * stick is pressed in a certain direction. However, these are not the only
 * uses.
 */
public enum Direction {

	UP, DOWN, LEFT, RIGHT;

	/* @formatter: off */
	@EnumAlias
	public static final Direction
			NORTH = UP,
			SOUTH = DOWN,
			EAST = RIGHT,
			WEST = LEFT;
	/* @formatter: on */

}
