package com.whirvis.controller;

public class AnalogTrigger extends DeviceAnalog<Trigger1fc> {

	/**
	 * @param id
	 *            the analog trigger ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public AnalogTrigger(String id) {
		super(id);
	}

	@Override
	public Trigger1fc initial() {
		return new Trigger1f();
	}

}
