package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.feature.AnalogTrigger;

/**
 * An {@link AnalogTrigger} mapping for use with a
 * {@link USBGameCubeControllerAdapter}.
 */
public class USBGamecubeAnalogTriggerMapping
		extends USBGameCubeAnalogMapping<AnalogTrigger> {

	public final int gcAxis;
	public final int min, max;

	/**
	 * Constructs a new {@code GamecubeAnalogTriggerMapping}.
	 * 
	 * @param analog
	 *            the trigger being mapped to.
	 * @param gcAxis
	 *            the Gamecube trigger axis.
	 * @param min
	 *            the minimum value of the axis.
	 * @param max
	 *            the maximum value of the axis.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public USBGamecubeAnalogTriggerMapping(AnalogTrigger analog, int gcAxis,
			int min, int max) {
		super(analog);
		this.gcAxis = gcAxis;
		this.min = min;
		this.max = max;
	}

}
