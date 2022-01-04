package com.whirvis.kibasan;

import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.feature.AnalogStick;
import com.whirvis.kibasan.feature.AnalogTrigger;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.FeaturePresent;

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
	 *            the controller ID, should be {@code null} if the
	 *            {@link DeviceId} annotation is present for this class.
	 * @param adapter
	 *            the PlayStation controller adapter.
	 * @param lt
	 *            the left analog trigger, may be {@code null}.
	 * @param rt
	 *            the right analog trigger, may be {@code null}.
	 * @throws IllegalArgumentException
	 *             if the {@link DeviceId} annotation is present and {@code id}
	 *             is not {@code null}.
	 * @throws NullPointerException
	 *             if no ID was specified for this device; if {@code adapter} is
	 *             {@code null}.
	 */
	public PsxController(String id,
			DeviceAdapter<?> adapter, AnalogTrigger lt, AnalogTrigger rt) {
		super(id, adapter, LS, RS, lt, rt);
	}

	/**
	 * @param adapter
	 *            the PlayStation controller adapter.
	 * @param lt
	 *            the left analog trigger, may be {@code null}.
	 * @param rt
	 *            the right analog trigger, may be {@code null}.
	 * @throws NullPointerException
	 *             if no ID was specified for this device; if {@code adapter} is
	 *             {@code null}.
	 */
	public PsxController(DeviceAdapter<?> adapter,
			AnalogTrigger lt, AnalogTrigger rt) {
		this(null, adapter, lt, rt);
	}

}
