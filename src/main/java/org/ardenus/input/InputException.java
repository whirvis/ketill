package org.ardenus.input;

/**
 * Signals that an error relating to the input system has occurred.
 */
public class InputException extends RuntimeException {

	private static final long serialVersionUID = 2296743015689493731L;

	/**
	 * Constructs a new {@code InputException} with the specified detail message
	 * and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link Throwable#getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.
	 */
	public InputException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@code InputException} with the specified detail
	 * message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link Throwable#getMessage()} method).
	 */
	public InputException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code InputException} with the specified cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 * 
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link Throwable#getCause()} method). A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.
	 */
	public InputException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new {@code InputException} with no detail message.
	 */
	public InputException() {
		super();
	}

}
