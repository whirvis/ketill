package com.whirvis.kibasan.adapter;

import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeatureMapping;
import com.whirvis.kibasan.feature.DeviceButton;

/**
 * A {@link DeviceButton} mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a button mapping cannot provide a meaningful mapping for an
 * button feature. It must be extended by a class which provides information
 * meaningful to the context of a relevant device adapter. This can be as simple
 * as providing an extra field for a button ID. An example of this would be:
 * 
 * <pre>
 * public class GlfwButtonMapping extends ButtonMapping {
 * 
 * 	public final int glfwButton;
 * 
 * 	public GlfwButtonMapping(DeviceButton button, int glfwButton) {
 * 		super(button);
 * 		this.glfwButton = glfwButton;
 * 	}
 * 
 * }
 * </pre>
 * 
 * @see AnalogMapping
 * @see RumbleMapping
 */
public abstract class ButtonMapping extends FeatureMapping<DeviceButton> {

	/**
	 * @param button
	 *            the button being mapped to.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	public ButtonMapping(DeviceButton button) {
		super(button);
	}

}
