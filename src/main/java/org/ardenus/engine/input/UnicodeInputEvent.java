package org.ardenus.engine.input;

public class UnicodeInputEvent extends InputEvent {

	private final Object source;
	private final char input;

	/**
	 * @param source
	 *            the source of input, may be {@code null}.
	 * @param input
	 *            the typed character.
	 */
	public UnicodeInputEvent(Object source, char input) {
		this.source = source;
		this.input = input;
	}

	/**
	 * @param input
	 *            the typed character.
	 */
	public UnicodeInputEvent(char input) {
		this(null, input);
	}

	/**
	 * @return the source of input, may be {@code null}.
	 */
	public Object getSource() {
		return this.source;
	}

	/**
	 * @return the typed character.
	 */
	public char getInput() {
		return this.input;
	}

}
