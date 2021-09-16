package org.ardenus.engine.input.adapter;

import java.util.Objects;

import org.ardenus.engine.input.device.DeviceButton;

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
 * 		this.glfwBUtton = glfwButton;
 * 	}
 * 
 * }
 * </pre>
 * 
 * @see ButtonMapping
 */
public abstract class MappedButton {

	public final DeviceButton button;

	/**
	 * Constructs a new {@code MappedButton}.
	 * 
	 * @param button
	 *            the button being mapped to.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	public MappedButton(DeviceButton button) {
		this.button = Objects.requireNonNull(button, "button");
	}

}
