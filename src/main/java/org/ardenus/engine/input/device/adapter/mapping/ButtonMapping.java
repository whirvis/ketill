package org.ardenus.engine.input.device.adapter.mapping;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.button.DeviceButton;

/**
 * A {@link DeviceButton} mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a mapped button can not provide a meaningful mapping for a
 * device button. It must be extended by a class which provides information
 * meaningful to the context of a relevant device adapter. This can be as simple
 * as providing an extra field for a button ID. An example of this would be:
 * 
 * <pre>
 * public class GLFWMappedButton extends MappedButton {
 * 
 * 	public final int glfwButton;
 * 
 * 	public GLFWMappedButton(DeviceButton button, int glfwButton) {
 * 		super(button);
 * 		this.glfwButton = glfwButton;
 * 	}
 * 
 * }
 * </pre>
 * 
 * @param <B>
 *            the device button type.
 * @see FeatureMapping
 * @see AnalogMapping
 */
public abstract class ButtonMapping extends FeatureMapping<DeviceButton> {

	/**
	 * Constructs a new {@code MappedButton}.
	 * 
	 * @param button
	 *            the button being mapped to.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	public ButtonMapping(DeviceButton button) {
		super(button);
	}

}
