package com.whirvis.kibasan;

import java.awt.Color;

import org.joml.Vector4f;
import org.joml.Vector4fc;

import com.whirvex.event.EventManager;
import com.whirvis.kibasan.adapter.DeviceAdapter;
import com.whirvis.kibasan.event.InputEvent;
import com.whirvis.kibasan.feature.AnalogTrigger;
import com.whirvis.kibasan.feature.DeviceButton;
import com.whirvis.kibasan.feature.FeaturePresent;
import com.whirvis.kibasan.feature.Lightbar;
import com.whirvis.kibasan.feature.RumbleMotor;

/**
 * A Sony PlayStation 4 controller.
 */
@DeviceId("ps4")
public class Ps4Controller extends PsxController {

	/**
	 * Signals that it has become ambiguous which PS4 controllers are which.
	 * <p>
	 * It is possible that a PlayStation 4 controller will report itself as two
	 * devices to one machine. This situation usually occurs when a controller
	 * that has been paired via Bluetooth is also connected via USB. There is no
	 * way to determine which PS4 controllers connected via USB and Bluetooth
	 * are the same device in the real world. As such, this event is used as a
	 * way to inform the program that it may be receiving dual input from the
	 * same device. How this situation is handled is up to the program.
	 */
	public static class AmbigousEvent extends InputEvent {

		private boolean resolved;

		/**
		 * @param resolved
		 *            {@code true} if the ambiguity has been resolved,
		 *            {@code false} otherwise.
		 */
		public AmbigousEvent(boolean resolved) {
			this.resolved = resolved;
		}

		/**
		 * @return {@code true} if the ambiguity has been resolved,
		 *         {@code false} otherwise.
		 */
		public boolean isResolved() {
			return this.resolved;
		}

	}

	/* @formatter: off */
	@FeaturePresent
	public static final DeviceButton
			SHARE = new DeviceButton("share"),
			OPTIONS = new DeviceButton("options"),
			PS = new DeviceButton("playstation"),
			TPAD = new DeviceButton("trackpad");
	
	@FeaturePresent
	public static final AnalogTrigger
			LT = new AnalogTrigger("lt"),
			RT = new AnalogTrigger("rt");
	
	@FeaturePresent
	public static final RumbleMotor
			RUMBLE_STRONG = new RumbleMotor("strong_rumble"),
			RUMBLE_WEAK = new RumbleMotor("weak_rumble");
	
	@FeaturePresent
	public static final Lightbar
			LIGHTBAR = new Lightbar("lightbar");
	/* @formatter: on */

	/**
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param adapter
	 *            the PlayStation 4 controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Ps4Controller(EventManager events,
			DeviceAdapter<Ps4Controller> adapter) {
		super(events, adapter, LT, RT);
	}

	/**
	 * @return the current lightbar color.
	 */
	public Vector4fc getLightbar() {
		return this.getState(LIGHTBAR);
	}

	/**
	 * Sets the current lightbar color.
	 * <p>
	 * The intensity of each color channel should be on a scale of {@code 0.0F}
	 * to {@code 1.0F}.<br>
	 * Values outside of this range will lead to unexpected results!
	 * 
	 * @param red
	 *            the red channel intensity.
	 * @param green
	 *            the green channel intensity.
	 * @param blue
	 *            the blue channel intensity.
	 * @param alpha
	 *            the alpha channel intensity.
	 */
	public void setLightbar(float red, float green, float blue, float alpha) {
		Vector4f color = this.getState(LIGHTBAR);
		color.x = red;
		color.y = green;
		color.z = blue;
		color.w = alpha;
	}

	/**
	 * Sets the current lightbar color.
	 * <p>
	 * The intensity of each color channel should be on a scale of {@code 0.0F}
	 * to {@code 1.0F}.<br>
	 * Values outside of this range will lead to unexpected results!
	 * <p>
	 * This method is a shorthand for
	 * {@link #setLightbar(float, float, float, float)}, with the argument for
	 * {@code alpha} being set to {@code 1.0F}.
	 * 
	 * @param red
	 *            the red channel intensity.
	 * @param green
	 *            the green channel intensity.
	 * @param blue
	 *            the blue channel intensity.
	 */
	public void setLightbar(float red, float green, float blue) {
		this.setLightbar(red, green, blue, 1.0F);
	}

	/**
	 * Sets the current lightbar color.
	 * 
	 * @param rgba
	 *            the RGBA color value.
	 * @param useAlpha
	 *            {@code true} if the alpha channel of {@code rgba} should be
	 *            used, {@code false} if it should be discarded.
	 */
	public void setLightbar(int rgba, boolean useAlpha) {
		Vector4f color = this.getState(LIGHTBAR);
		color.x = ((byte) (rgba >> 0)) / 255.0F;
		color.y = ((byte) (rgba >> 8)) / 255.0F;
		color.z = ((byte) (rgba >> 16)) / 255.0F;
		if (useAlpha) {
			color.w = ((byte) (rgba >> 24)) / 255.0F;
		} else {
			color.w = 1.0F;
		}
	}

	/**
	 * Sets the current lightbar color.
	 * <p>
	 * This method is a shorthand for {@link #setLightbar(int, boolean)}, with
	 * the argument {@code rgba} being {@code rgb} and the argument for
	 * {@code useAlpha} being {@code false}.
	 * 
	 * @param rgb
	 *            the RGB color value.
	 */
	public void setLightbar(int rgb) {
		this.setLightbar(rgb, false);
	}

	/**
	 * Sets the current lightbar color.
	 * 
	 * @param color
	 *            the color, may be {@code null}.
	 */
	public void setLightbar(Color color) {
		if (color == null) {
			this.setLightbar(0x00000000);
		} else {
			this.setLightbar(color.getRGB());
		}
	}

}
