package com.whirvis.kibasan;

import com.whirvis.kibasan.feature.Button1bc;
import com.whirvis.kibasan.feature.Cursor;
import com.whirvis.kibasan.feature.Cursor2f;
import com.whirvis.kibasan.feature.DeviceButton;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * A mouse which can send and receive input data.
 * <p>
 * <b>Note:</b> For a mouse to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the mouse once on every application update.
 * 
 * @see Keyboard
 * @see Controller
 */
@DeviceId("mouse")
public class Mouse extends InputDevice {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			BUTTON_1 = new DeviceButton("mouse_1"),
			BUTTON_2 = new DeviceButton("mouse_2"),
			BUTTON_3 = new DeviceButton("mouse_3"),
			BUTTON_4 = new DeviceButton("mouse_4"),
			BUTTON_5 = new DeviceButton("mouse_5"),
			BUTTON_6 = new DeviceButton("mouse_6"),
			BUTTON_7 = new DeviceButton("mouse_7"),
			BUTTON_8 = new DeviceButton("mouse_8");
	
	public static final DeviceButton
			LEFT = BUTTON_1,
			RIGHT = BUTTON_2,
			MIDDLE = BUTTON_3;
	
	@FeaturePresent
	public static final Cursor
			CURSOR = new Cursor("mouse_cursor");
	/* @formatter: on */

	/**
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Mouse(DeviceAdapter<Mouse> adapter) {
		super(adapter);
	}

	/**
	 * @param button
	 *            the mouse button.
	 * @return {@code true} if {@code button} is pressed, {@code false}
	 *         otherwise.
	 */
	public boolean isPressed(DeviceButton button) {
		Button1bc state = this.getState(button);
		return state.pressed();
	}

	/**
	 * @return the cursor position.
	 */
	public Vector2fc getPosition() {
		return this.getState(CURSOR);
	}

	/**
	 * @return the X-axis position of the cursor.
	 */
	public float getX() {
		Vector2fc pos = this.getState(CURSOR);
		return pos.x();
	}

	/**
	 * @return the Y-axis position of the cursor.
	 */
	public float getY() {
		Vector2fc pos = this.getState(CURSOR);
		return pos.y();
	}

	/**
	 * @return the position of the cursor.
	 */
	public Vector2fc getPos() {
		return this.getState(CURSOR);
	}

	/**
	 * @param pos
	 *            the position the cursor should be at.
	 */
	public void setPosition(Vector2fc pos) {
		Cursor2f cursor = this.getState(CURSOR);
		cursor.requestPos(pos);
	}

	/**
	 * @param x
	 *            the X-axis position the cursor should be at.
	 * @param y
	 *            the Y-axis position the cursor should be at.
	 */
	public void setPosition(float x, float y) {
		this.setPosition(new Vector2f(x, y));
	}

	/**
	 * @return {@code true} if the cursor is visible, {@code false} otherwise.
	 */
	public boolean isVisible() {
		Cursor2f cursor = this.getState(CURSOR);
		return cursor.visible;
	}

	/**
	 * @param visible
	 *            {@code true} if the cursor should be visible, {@code false}
	 *            otherwise.
	 */
	public void setVisible(boolean visible) {
		Cursor2f cursor = this.getState(CURSOR);
		cursor.visible = visible;
	}

}
