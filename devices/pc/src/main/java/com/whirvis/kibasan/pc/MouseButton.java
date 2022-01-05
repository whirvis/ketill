package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;

public class MouseButton implements DeviceFeature<Button1bc> {

	private final String id;

	/**
	 * @param id
	 *            the button ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public MouseButton(String id) {
		this.id = Objects.requireNonNull(id, "id");
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public Button1bc initial() {
		return new Button1b();
	}

}
