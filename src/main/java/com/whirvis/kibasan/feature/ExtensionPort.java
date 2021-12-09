package com.whirvis.kibasan.feature;

import java.util.Objects;

public class ExtensionPort<T> implements DeviceFeature<Extension1ec<T>> {

	private final String id;

	/**
	 * @param id
	 *            the extension port ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public ExtensionPort(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Extension1ec<T> initial() {
		return new Extension1e<T>();
	}

}
