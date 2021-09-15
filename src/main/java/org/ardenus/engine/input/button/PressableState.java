package org.ardenus.engine.input.button;

/**
 * A container for the state of a {@link Pressable} instance.
 * <p>
 * When used, instances of this container should not be accessible to outside
 * sources. If returned directly, the state could also be modified rather than
 * only read. As such, getter methods should be used to return only what need be
 * exposed to outside sources, in a read only form. E.G.,
 * {@code isPressed(Pressable)}, {@code getPressTime(Pressable)}, etc.
 */
public class PressableState implements Cloneable {

	public boolean pressed;
	public long pressTime_ms;
	public long releaseTime_ms;

	public boolean held;
	public long holdPressTime_ms;

	/**
	 * A container should only be cloned to make a copy of the previous state
	 * before an update. E.G., seeing if a button was not pressed but is now
	 * pressed. It should not be used to return the current state in a getter
	 * method!
	 */
	@Override
	public PressableState clone() {
		try {
			return (PressableState) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}
