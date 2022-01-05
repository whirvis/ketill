package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class Cursor extends DeviceFeature<Cursor2f> {

	/**
	 * @param id
	 *            the cursor ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public Cursor(String id) {
		super(id, Cursor2f::new);
	}

}
