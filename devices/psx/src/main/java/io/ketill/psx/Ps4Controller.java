package io.ketill.psx;

import io.ketill.AdapterSupplier;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.Button1bc;
import io.ketill.controller.DeviceButton;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.controller.RumbleMotor;
import io.ketill.controller.Vibration1f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import java.awt.*;

/**
 * A Sony PlayStation 4 controller.
 */
public class Ps4Controller extends PsxController {

    private static float capValue(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    /* @formatter:off */
    @FeaturePresent
    public static final @NotNull DeviceButton
            BUTTON_SHARE = new DeviceButton("share"),
            BUTTON_OPTIONS = new DeviceButton("options"),
            BUTTON_PS = new DeviceButton("playstation"),
            BUTTON_TPAD = new DeviceButton("trackpad");

    @FeaturePresent
    public static final @NotNull AnalogTrigger
            TRIGGER_LT = new AnalogTrigger("lt"),
            TRIGGER_RT = new AnalogTrigger("rt");

    @FeaturePresent
    public static final @NotNull RumbleMotor
            MOTOR_STRONG = new RumbleMotor("rumble_strong"),
            MOTOR_WEAK = new RumbleMotor("rumble_weak");

    @FeaturePresent
    public static final @NotNull Lightbar
            FEATURE_LIGHTBAR = new Lightbar("lightbar");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState
    public final @NotNull Button1bc
            share = this.getState(BUTTON_SHARE),
            options = this.getState(BUTTON_OPTIONS),
            ps = this.getState(BUTTON_PS),
            tpad = this.getState(BUTTON_TPAD);

    @FeatureState
    public final @NotNull Vibration1f
            rumbleStrong = this.getState(MOTOR_STRONG),
            rumbleWeak = this.getState(MOTOR_WEAK);

    @FeatureState
    public final @NotNull Vector4f
            lightbar = this.getState(FEATURE_LIGHTBAR);
    /* @formatter:on */

    public Ps4Controller(@NotNull AdapterSupplier<Ps4Controller> adapterSupplier) {
        super("ps4", adapterSupplier, TRIGGER_LT, TRIGGER_RT);
    }

    /**
     * To prevent unexpected behavior, the intensity of each color channel
     * is capped between a value of {@code 0.0F} and {@code 1.0F}.
     *
     * @param red   the red channel intensity.
     * @param green the green channel intensity.
     * @param blue  the blue channel intensity.
     * @param alpha the alpha channel intensity.
     */
    public void setLightbarColor(float red, float green, float blue,
                                 float alpha) {
        float min = 0.0F, max = 1.0F;
        lightbar.x = capValue(red, min, max);
        lightbar.y = capValue(green, min, max);
        lightbar.z = capValue(blue, min, max);
        lightbar.w = capValue(alpha, min, max);
    }

    /**
     * To prevent unexpected behavior, the intensity of each color channel
     * is capped between a value of {@code 0.0F} and {@code 1.0F}.
     * <p>
     * This method is a shorthand for
     * {@link #setLightbarColor(float, float, float, float)}, with the
     * argument for {@code alpha} being set to {@code 1.0F}.
     *
     * @param red   the red channel intensity.
     * @param green the green channel intensity.
     * @param blue  the blue channel intensity.
     */
    public void setLightbarColor(float red, float green, float blue) {
        this.setLightbarColor(red, green, blue, 1.0F);
    }

    /**
     * @param rgba     the RGBA color value.
     * @param useAlpha {@code true} if the alpha channel of {@code rgba}
     *                 should be used, {@code false} to have it discarded.
     */
    public void setLightbarColor(int rgba, boolean useAlpha) {
        lightbar.x = ((byte) rgba) / 255.0F;
        lightbar.y = ((byte) (rgba >> 8)) / 255.0F;
        lightbar.z = ((byte) (rgba >> 16)) / 255.0F;
        if (useAlpha) {
            lightbar.w = ((byte) (rgba >> 24)) / 255.0F;
        } else {
            lightbar.w = 1.0F;
        }
    }

    /**
     * This method is a shorthand for {@link #setLightbarColor(int, boolean)},
     * with the argument for {@code rgba} being {@code rgb} and the argument
     * for {@code useAlpha} being {@code false}.
     *
     * @param rgb the RGB color value.
     */
    public void setLightbarColor(int rgb) {
        this.setLightbarColor(rgb, false);
    }

    /**
     * This method is a shorthand for {@link #setLightbarColor(int, boolean)},
     * with the argument for {@code rgba} being {@code color.getRGB()} (or
     * {@code 0x00000000} if {@code color} is {@code null}) and the argument
     * for {@code useAlpha} being {@code true}.
     *
     * @param color the color value.
     */
    public void setLightbarColor(@Nullable Color color) {
        if (color == null) {
            this.setLightbarColor(0x00000000, true);
        } else {
            this.setLightbarColor(color.getRGB(), true);
        }
    }

}
