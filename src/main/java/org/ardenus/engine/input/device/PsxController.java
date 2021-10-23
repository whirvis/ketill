package org.ardenus.engine.input.device;

import org.ardenus.engine.input.Direction;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.AnalogStick;
import org.ardenus.engine.input.device.feature.AnalogTrigger;
import org.ardenus.engine.input.device.feature.DeviceButton;
import org.ardenus.engine.input.device.feature.FeaturePresent;

/**
 * A Sony PlayStation controller.
 */
public abstract class PsxController extends Controller {

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			SQUARE = new DeviceButton("square"),
			CROSS = new DeviceButton("cross"),
			CIRCLE = new DeviceButton("circle"),
			TRIANGLE = new DeviceButton("triangle"),
			L1 = new DeviceButton("l1"),
			R1 = new DeviceButton("r1"),
			L2 = new DeviceButton("l2"),
			R2 = new DeviceButton("r2"),
			THUMB_L = new DeviceButton("ls"),
			THUMB_R = new DeviceButton("rs"),
			UP = new DeviceButton("up", Direction.UP),
			RIGHT = new DeviceButton("right", Direction.RIGHT),
			DOWN = new DeviceButton("down", Direction.DOWN),
			LEFT = new DeviceButton("left", Direction.LEFT);
	
	@FeaturePresent
	public static final AnalogStick
			LS = new AnalogStick("ls", THUMB_L),
			RS = new AnalogStick("rs", THUMB_R);
	/* @formatter: on */

	/**
	 * @param id
	 *            the controller ID.
	 * @param adapter
	 *            the PlayStation controller adapter.
	 * @param lt
	 *            the left analog trigger, may be {@code null}.
	 * @param rt
	 *            the right analog trigger, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code id} or {@code adapter} are {@code null}.
	 */
	public PsxController(String id, DeviceAdapter<?> adapter, AnalogTrigger lt,
			AnalogTrigger rt) {
		super(id, adapter, LS, RS, lt, rt);
	}

}
