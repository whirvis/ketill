package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class MouseButton extends DeviceFeature<Button1bc> {

	/**
	 * @param id
	 *            the button ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public MouseButton(String id) {
		super(id, Button1b::new);
	}

}
