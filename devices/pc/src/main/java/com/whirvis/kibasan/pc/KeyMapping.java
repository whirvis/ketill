package com.whirvis.kibasan.pc;

import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeatureMapping;

/**
 * A {@link KeyboardKey} mapping for use with a {@link DeviceAdapter}.
 * <p>
 * On their own, a key mapping cannot provide a meaningful mapping for a
 * keyboard key feature. It must be extended by a class which provides
 * information meaningful to the context of a relevant device adapter. This can
 * be as simple as providing an extra field for a key ID. An example of this
 * would be:
 * 
 * <pre>
 * public class GlfwKeyMapping extends KeyMapping {
 * 
 * 	public final int glfwKey;
 * 
 * 	public GlfwKeyMapping(DeviceButton button, int glfwKey) {
 * 		super(button);
 * 		this.glfwKey = glfwKey;
 * 	}
 * 
 * }
 * </pre>
 */
public abstract class KeyMapping extends FeatureMapping<KeyboardKey> {

	/**
	 * @param key
	 *            the key being mapped to.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public KeyMapping(KeyboardKey key) {
		super(key);
	}

}
