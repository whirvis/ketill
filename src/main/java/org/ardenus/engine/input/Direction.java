package org.ardenus.engine.input;

/**
 * Represents a generic direction.
 * <p>
 * Directions, like buttons, can be pressable. Their state is usually
 * represented by buttons and/or analog sticks. For example, a controller may
 * press up on the D-pad, push upward on the analog stick, and then release the
 * D-pad's up button. Even though these are different parts of the controller,
 * the {@code UP} direction will still be considered pressed. However, it is not
 * a requirement that {@code Direction} be used in this exact way.
 */
public enum Direction {

	UP, DOWN, LEFT, RIGHT;

}
