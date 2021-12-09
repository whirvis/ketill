package com.whirvis.kibasan;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.DeviceOrientation;
import com.whirvis.kibasan.feature.FeaturePresent;
import com.whirvis.kibasan.feature.Orientation1o;
import com.whirvis.kibasan.feature.PlayerLed;
import com.whirvis.kibasan.feature.RumbleMotor;

/**
 * A Nintendo Wii controller.
 */
@DeviceId("wii")
public class Wiimote extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			LEFT = new DeviceButton("left", Direction.LEFT),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			UP = new DeviceButton("up", Direction.UP),
			PLUS = new DeviceButton("plus"),	
			TWO = new DeviceButton("two"),
			ONE = new DeviceButton("one"),
			B = new DeviceButton("b"),
			A = new DeviceButton("a"),
			MINUS = new DeviceButton("minus"),
			HOME = new DeviceButton("home");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE = new RumbleMotor("rumble");
	
	@FeaturePresent
	public static final PlayerLed
			PLAYER_LED = new PlayerLed("player_led");
	
	@FeaturePresent
	public static final DeviceOrientation
			ORIENTATION = new DeviceOrientation("orientation");
	/* @formatter: on */

	/**
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the Wiimote adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Wiimote(EventManager events, DeviceAdapter<Wiimote> adapter) {
		super(events, adapter, null, null, null, null);
	}

	/**
	 * The orientation of the Wiimote determines how some buttons are mapped.
	 * For example, the physical buttons on the D-pad represent different device
	 * buttons depending on the orientation. This does <i>not</i> represent the
	 * orientation in which the controller is being held (use the accelerometer
	 * to determine this.)
	 * 
	 * @return the Wiimote's orientation.
	 * @see #setOrientation(Direction)
	 */
	public Direction getOrientation() {
		Orientation1o state = this.getState(ORIENTATION);
		return state.getDirection();
	}

	/**
	 * @param orientation
	 *            the orientation to set the Wiimote to.
	 * @throws NullPointerException
	 *             if {@code orientation} is {@code null}.
	 * @see #getOrientation()
	 */
	public void setOrientation(Direction orientation) {
		Orientation1o state = this.getState(ORIENTATION);
		state.setDirection(orientation);
	}

}
