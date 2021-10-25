package org.ardenus.engine.input.device;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.Cursor;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.ardenus.engine.input.device.feature.monitor.DeviceButtonMonitor;
import org.ardenus.engine.util.FieldAlias;
import org.joml.Vector2fc;

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
	
	@FieldAlias
	public static final DeviceButton
			LEFT = BUTTON_1,
			RIGHT = BUTTON_2,
			MIDDLE = BUTTON_3;
	
	@FeaturePresent
	public static final Cursor
			CURSOR = new Cursor("mouse_cursor");
	/* @formatter: on */

	/**
	 * By default, a {@code Mouse} expects device features of type
	 * {@link DeviceButton}. As a result, an instance of
	 * {@link DeviceButtonMonitor} will be added on instantiation.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Mouse(DeviceAdapter<Mouse> adapter) {
		super(adapter);
		this.addMonitor(new DeviceButtonMonitor(this));
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

}
