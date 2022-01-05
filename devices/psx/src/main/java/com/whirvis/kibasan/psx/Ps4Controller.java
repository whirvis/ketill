package com.whirvis.kibasan.psx;

import com.whirvis.controller.AnalogTrigger;
import com.whirvis.controller.DeviceButton;
import com.whirvis.controller.RumbleMotor;
import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.FeaturePresent;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.awt.*;

/**
 * A Sony PlayStation 4 controller.
 */
public class Ps4Controller extends PsxController {

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
	 * @param adapter
	 *            the PlayStation 4 controller adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Ps4Controller(DeviceAdapter<Ps4Controller> adapter) {
		super("ps4", adapter, LT, RT);
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
