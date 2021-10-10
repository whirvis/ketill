package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.feature.DeviceButton;

/**
 * A {@link DeviceButton} mapping for use with a
 * {@link USBGameCubeControllerAdapter}.
 */
public class USBGameCubeButtonMapping extends ButtonMapping {

	public final int gcButton;

	/**
	 * Constructs a new {@code GamecubeButtonMapping}.
	 * 
	 * @param button
	 *            the button being mapped to.
	 * @param gcButton
	 *            the Gamecube button ID.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	public USBGameCubeButtonMapping(DeviceButton button, int gcButton) {
		super(button);
		this.gcButton = gcButton;
	}

}
