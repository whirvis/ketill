package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.feature.AnalogStick;

/**
 * An {@link AnalogStick} mapping for use with a
 * {@link USBGameCubeControllerAdapter}.
 */
public class USBGameCubeAnalogStickMapping
		extends USBGameCubeAnalogMapping<AnalogStick> {

	public final int gcAxisX;
	public final int gcAxisY;
	public final int xMin, xMax;
	public final int yMin, yMax;

	/**
	 * Constructs a new {@code GamecubeAnalogStickMapping}.
	 * 
	 * @param analog
	 *            the stick being mapped to.
	 * @param glfwAxisX
	 *            the Gamecube stick X-axis.
	 * @param glfwAxisY
	 *            the Gamecube stick Y-axis.
	 * @param xMin
	 *            the minimum value of the X-axis stick.
	 * @param xMax
	 *            the maximum value of the X-axis stick.
	 * @param yMin
	 *            the minimum value of the Y-axis stick.
	 * @param yMax
	 *            the maximum value of the Y-axis stick.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public USBGameCubeAnalogStickMapping(AnalogStick analog, int gcAxisX,
			int gcAxisY, int xMin, int xMax, int yMin, int yMax) {
		super(analog);
		this.gcAxisX = gcAxisX;
		this.gcAxisY = gcAxisY;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

}
