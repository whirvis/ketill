package com.whirvis.kibasan.psx;

import com.whirvis.controller.DeviceAnalog;
import org.joml.Vector4f;

public class Lightbar extends DeviceAnalog<Vector4f> {

	/**
	 * @param id
	 *            the lightbar ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public Lightbar(String id) {
		super(id, Vector4f::new);
	}

}
