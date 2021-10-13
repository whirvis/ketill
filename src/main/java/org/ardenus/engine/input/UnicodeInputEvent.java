package org.ardenus.engine.input;

/**
 * Signals that a Unicode character has been typed.
 */
public class UnicodeInputEvent extends InputEvent {

	private final char c;

	/**
	 * Constructs a new {@code UnicodeInputEvent}.
	 * 
	 * @param c
	 *            the character type.
	 */
	public UnicodeInputEvent(char c) {
		this.c = c;
	}

	/**
	 * Returns the typed character.
	 * 
	 * @return the typed character.
	 */
	public char getChar() {
		return this.c;
	}

}
