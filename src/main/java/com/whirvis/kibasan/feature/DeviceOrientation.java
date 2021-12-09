package com.whirvis.kibasan.feature;

import java.util.Objects;

public class DeviceOrientation implements DeviceFeature<Orientation1o> {

	private final String id;

	/**
	 * @param id
	 *            the orientation ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public DeviceOrientation(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Orientation1o initial() {
		return new Orientation1o();
	}

}
