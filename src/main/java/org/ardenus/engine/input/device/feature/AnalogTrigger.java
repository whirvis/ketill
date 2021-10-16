package org.ardenus.engine.input.device.feature;

public class AnalogTrigger extends DeviceAnalog<Trigger1fc> {

	/**
	 * @param id
	 *            the analog trigger ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public AnalogTrigger(String id) {
		super(Trigger1fc.class, id);
	}

	@Override
	public Trigger1fc zero() {
		return new Trigger1f();
	}

}
