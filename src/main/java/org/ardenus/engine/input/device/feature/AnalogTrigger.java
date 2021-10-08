package org.ardenus.engine.input.device.feature;

/**
 * Represents an analog trigger on an input device.
 */
public class AnalogTrigger extends DeviceAnalog<Trigger1fc> {

	/**
	 * Constructs a new {@code AnalogTrigger}.
	 * 
	 * @param name
	 *            the analog trigger name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public AnalogTrigger(String name) {
		super(Trigger1fc.class, name);
	}

	@Override
	public Trigger1fc zero() {
		return new Trigger1f();
	}

}
