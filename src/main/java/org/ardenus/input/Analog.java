package org.ardenus.input;

import java.util.Objects;

/**
 * Represents an analog input.
 * <p>
 * Analog input objects come in multiple forms. Examples include, but are not
 * limited to: analog sticks, triggers, or gyroscopes. The internal value of
 * these analog inputs is usually stored via an instance of one of their
 * containers (which can be created via {@link #zero()}). However, this it not a
 * requirement by any means.
 * <p>
 * While this is also not a requirement, instances of {@code Analog} are usually
 * only the representation. An example of this would be:
 * 
 * <pre>
 * public class GameController {
 *  
 *	public static final Analog&lt;?&gt;
 *			LS = new AnalogStick("left_stick"),
 *			RS = new AnalogStick("right_stick");
 *
 *	private final Map&lt;Analog&lt;?&gt;, Object&gt; analogs;
 *
 *	public GameController() {
 *		this.analogs = new HashMap&lt;&gt;();
 *		analogs.put(LS, LS.zero());
 *		analogs.put(RS, RS.zero());
 *	}
 *
 *	&#64;SuppressWarnings("unchecked")
 *	public &lt;V&gt; V getValue(Analog&lt;V&gt; analog) {
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
public abstract class Analog<V> {

	public final Class<?> type;

	/**
	 * @param type
	 *            the value type class.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}.
	 */
	public Analog(Class<V> type) {
		this.type = Objects.requireNonNull(type, "type");
	}

	/**
	 * @return a zeroed out value container.
	 */
	public abstract V zero();

}
