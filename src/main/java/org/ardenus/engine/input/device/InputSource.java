package org.ardenus.engine.input.device;

/**
 * A source of input for an {@link InputDevice}.
 *
 * @param <T>
 *            the input value type.
 * @see InputDevice#addSource(InputSource)
 */
public interface InputSource<T> {

	/**
	 * Returns the name of this input source.
	 * 
	 * @return the name of this input source.
	 */
	public String name();

	/**
	 * Creates a container for the initial state of this input source.
	 * 
	 * @return the created container.
	 */
	public T initial();

}
