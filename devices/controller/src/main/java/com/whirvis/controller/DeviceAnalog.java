package com.whirvis.controller;

import com.whirvis.kibasan.DeviceFeature;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Represents an analog input on a device.
 * <p>
 * Analog input objects come in multiple forms. Examples include, but are not
 * limited to: analog sticks, triggers, or gyroscopes. The internal value of
 * these analog inputs is usually stored via an instance of one of their
 * containers (which can be created via {@link #initial}).
 * <p>
 * While this is also not a requirement, instances of {@code DeviceAnalog} are
 * usually only the representation.<br>
 * An example of this would be:
 * 
 * <pre>
 * public class GameController {
 *  
 *	public static final DeviceAnalog&lt;?&gt;
 *			LS = new AnalogStick("left_stick"),
 *			RS = new AnalogStick("right_stick");
 *
 *	private final Map&lt;DeviceAnalog&lt;?&gt;, Object&gt; analogs;
 *
 *	public GameController() {
 *		this.analogs = new HashMap&lt;&gt;();
 *		analogs.put(LS, LS.initial.get());
 *		analogs.put(RS, RS.initial.get());
 *	}
 *
 *	&#64;SuppressWarnings("unchecked")
 *	public &lt;V&gt; V getValue(DeviceAnalog&lt;V&gt; analog) {
 *		if (analog == null) {
 *			return null;
 *		}
 *    
 *		&sol;*
 *		 * As can be seen in this example, LS and RS will be reused
 *		 * for different instances of GameController. Each controller
 *		 * contains a map of each analog, with the stored value being
 *		 * the position of each analog via their container class. This
 *		 * works out well for groups of analogs known in advance.
 *		 *&sol;
 *		Object value = analogs.get(analog);
 *		return (V) value;
 *	}
 * 
 * }
 * </pre>
 * 
 * @param <V>
 *            the value type.
 */
public abstract class DeviceAnalog<V> extends DeviceFeature<V> {

	/**
	 * @param id
	 *            the analog input ID.
	 * @param initial
	 * 			a supplier for a container of this feature's initial state.
	 * @throws NullPointerException
	 *             if {@code type} or {@code id} are {@code null}.
	 */
	public DeviceAnalog(String id, Supplier<V> initial) {
		super(id, initial);
	}

}
