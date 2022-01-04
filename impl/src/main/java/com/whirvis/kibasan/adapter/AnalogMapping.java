package com.whirvis.kibasan.adapter;

import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeatureMapping;
import com.whirvis.kibasan.feature.DeviceAnalog;

/**
 * A {@link DeviceAnalog} mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, an analog mapping cannot provide a meaningful mapping for an
 * analog feature. It must be extended by a class which provides information
 * meaningful to the context of a relevant device adapter. This can be as simple
 * as providing an extra field for an analog ID. An example of this would be:
 * 
 * <pre>
 * public class GlfwAnalogMapping extends AnalogMapping {
 * 
 * 	public final int glfwAxis;
 * 
 * 	public GlfwAnalogMapping(DeviceAnalog analog, int glfwAxis) {
 * 		super(analog);
 * 		this.glfwAxis = glfwAxis;
 * 	}
 * 
 * }
 * </pre>
 * 
 * @param <A>
 *            the device analog type.
 * @see ButtonMapping
 * @see RumbleMapping
 */
public abstract class AnalogMapping<A extends DeviceAnalog<?>>
		extends FeatureMapping<A> {

	/**
	 * @param analog
	 *            the analog being mapped to.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	public AnalogMapping(A analog) {
		super(analog);
	}

}
