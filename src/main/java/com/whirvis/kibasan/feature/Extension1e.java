package com.whirvis.kibasan.feature;

public class Extension1e<T> implements Extension1ec<T> {

	public T connected;

	/**
	 * @param connected
	 *            the connected extension, may be {@code null}.
	 */
	public Extension1e(T connected) {
		this.connected = connected;
	}

	/**
	 * Constructs a new {@code Extension1e} with {@code connected} set to
	 * {@code null}.
	 */
	public Extension1e() {
		this(null);
	}

	@Override
	public T connected() {
		return this.connected;
	}

}
