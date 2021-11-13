package org.ardenus.input;

/**
 * Represents a button input.
 * <p>
 * Buttons come in multiple forms. Examples include, but are not limited to: a
 * keyboard key, a controller button, or a GUI element. The internal state of
 * these buttons is usually stored via an instance of a state container. This is
 * not a requirement by any means, however.
 * <p>
 * While this is also not a requirement, instances of {@code Button} are usually
 * only the representation. An example of this would be:
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
 *	public static final Button
 *			A = new DeviceButton("a"),
 *			B = new DeviceButton("b");
 *
 *	private final Map&lt;Button, ButtonState&gt; buttons;
 *
 *	public GameController() {
 *		this.buttons = new HashMap&lt;&gt;();
 *		buttons.put(A, new ButtonState());
 *		buttons.put(B, new ButtonState());
 *	}
 *
 *	public boolean isPressed(Button button) {
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
public abstract class Button {

	/**
	 * Unless overridden, this method will always return {@code true} by
	 * default.<br>
	 * It is up to the implementation of this class to determine whether or not
	 * a button is currently pressable.
	 * <p>
	 * If a button is not currently pressable, it does not mean, <i>and should
	 * <b>never</b> mean</i>, that it is currently pressed (i.e., it cannot be
	 * pressed further). The same goes for whether or not it is released. A
	 * button can become unpressable while in any state. However, this should
	 * usually only happen when it is released. Implementations should also
	 * automatically release buttons if they become unpressable while being
	 * pressed down. However, this is not a requirement.
	 * 
	 * @return {@code true} if this button can currently be pressed,
	 *         {@code false} otherwise.
	 */
	public boolean isPressable() {
		return true;
	}

}
