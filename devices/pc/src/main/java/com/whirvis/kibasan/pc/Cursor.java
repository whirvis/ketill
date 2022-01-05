package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class Cursor implements DeviceFeature<Cursor2f> {

	private final String id;

	/**
	 * @param id
	 *            the cursor ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public Cursor(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Cursor2f initial() {
		return new Cursor2f();
	}

}
