package com.whirvis.controller;

import com.whirvis.kibasan.DeviceFeature;
import com.whirvis.kibasan.Direction;

import java.util.Objects;

/**
 * Represents a button on a device.
 * <p>
 * Buttons come in multiple forms. Examples include, but are not limited to: a
 * keyboard key, a controller button, etc. The internal state of these buttons
 * is usually stored via an instance of a state container (which can be created
 * via {@link #initial()}). This is not a requirement by any means, however.
 * <p>
 * While this is also not a requirement, instances of {@code DeviceButton} are
 * usually only the representation.<br>
 * An example of this would be:
 * 
 * <pre>
 * public class GameController {
 *
 *	private static class ButtonState {
 *
 *		public boolean pressed;
 *
 *	}
 *
 *	public static final DeviceButton
 *			A = new DeviceButton("a"),
 *			B = new DeviceButton("b");
 *
 *	private final Map&lt;DeviceButton, ButtonState&gt; buttons;
 *
 *	public GameController() {
 *		this.buttons = new HashMap&lt;&gt;();
 *		buttons.put(A, new ButtonState());
 *		buttons.put(B, new ButtonState());
 *	}
 *
 *	public boolean isPressed(DeviceButton button) {
 *		&sol;*
 *		 * As can be seen in this example, A and B will be reused
 *		 * for different instances of GameController. Each controller
 *		 * contains a map of each button, with the stored value being
 *		 * the state of each button via the ButtonState class. This
 *		 * works out well for groups of buttons known in advance.
 *		 *&sol;
 *		ButtonState state = buttons.get(button);
 *		if (state != null) {
 *			return state.pressed;
 *		}
 *		return false;
 *	}
 * 
 * }
 * </pre>
 */
public class DeviceButton implements DeviceFeature<Button1bc> {

	private final String id;
	public final Direction direction;

	/**
	 * @param id
	 *            the button ID.
	 * @param direction
	 *            the direction this button represents. A {@code null} value is
	 *            permitted, and indicates that this button does not represent a
	 *            direction.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public DeviceButton(String id, Direction direction) {
		this.id = Objects.requireNonNull(id, "id");
		this.direction = direction;
	}

	/**
	 * @param id
	 *            the button ID.
	 * @throws NullPointerException
	 *             if {@code id} is {@code null}.
	 */
	public DeviceButton(String id) {
		this(id, null);
	}

	@Override
	public final String id() {
		return this.id;
	}

	@Override
	public Button1bc initial() {
		return new Button1b();
	}

}
