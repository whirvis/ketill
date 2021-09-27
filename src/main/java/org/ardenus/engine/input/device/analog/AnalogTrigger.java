package org.ardenus.engine.input.device.analog;

/**
 * Represents an analog trigger on an input device.
 */
public class AnalogTrigger extends DeviceAnalog<Trigger1f> {

	/**
	 * Constructs a new {@code AnalogTrigger}.
	 * 
	 * @param name
	 *            the analog trigger name.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	public AnalogTrigger(String name) {
		super(Trigger1f.class, name);
	}

	@Override
	public Trigger1f zero() {
		return new Trigger1f();
	}

}
