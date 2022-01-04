package com.whirvis.kibasan.feature;

public class Cursor extends DeviceAnalog<Cursor2f> {

	/**
	 * @param id
	 *            the cursor ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public Cursor(String id) {
		super(id);
	}

	@Override
	public Cursor2f initial() {
		return new Cursor2f();
	}

}
