package io.ketill.psx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import java.awt.*;

/**
 * Contains the state of a {@link Lightbar}.
 */
public final class LightbarColor {

    private static float cap(float value, float min, float max) {
        return Math.min(Math.max(value, min), max);
    }

    private final Vector4f vector;

    /**
     * Constructs a new {@code LightbarColor}.
     */
    public LightbarColor() {
        this.vector = new Vector4f();
    }

    /**
     * Returns the color in vector form.
     *
     * @return the color in vector form.
     */
    public @NotNull Vector4fc getVector() {
        return this.vector;
    }

    /**
     * Sets the color of the lightbar.
     * <p>
     * <b>Note:</b> To prevent unexpected behavior, the intensity of each
     * channel is capped between {@code 0.0F} and {@code 1.0F}.
     *
     * @param red   the red channel intensity.
     * @param green the green channel intensity.
     * @param blue  the blue channel intensity.
     * @param alpha the alpha channel intensity.
     */
    public void setColor(float red, float green, float blue,
                         float alpha) {
        float min = 0.0F, max = 1.0F;
        vector.x = cap(red, min, max);
        vector.y = cap(green, min, max);
        vector.z = cap(blue, min, max);
        vector.w = cap(alpha, min, max);
    }

    /**
     * Sets the color of the lightbar.
     * <p>
     * <b>Note:</b> To prevent unexpected behavior, the intensity of each
     * channel is capped between {@code 0.0F} and {@code 1.0F}.
     * <p>
     * <b>Shorthand for:</b> {@link #setColor(float, float, float, float)},
     * with the argument for {@code alpha} being {@code 1.0F}.
     *
     * @param red   the red channel intensity.
     * @param green the green channel intensity.
     * @param blue  the blue channel intensity.
     */
    public void setColor(float red, float green, float blue) {
        this.setColor(red, green, blue, 1.0F);
    }

    /**
     * Sets the color of the lightbar.
     *
     * @param rgba     the RGBA color value.
     * @param useAlpha {@code true} if the alpha channel of {@code rgba}
     *                 should be used, {@code false} to have it discarded.
     */
    public void setColor(int rgba, boolean useAlpha) {
        vector.x = (((byte) (rgba >> 24)) & 0xFF) / 255.0F;
        vector.y = (((byte) (rgba >> 16)) & 0xFF) / 255.0F;
        vector.z = (((byte) (rgba >> 8)) & 0xFF) / 255.0F;
        if (useAlpha) {
            vector.w = (((byte) rgba) & 0xFF) / 255.0F;
        } else {
            vector.w = 1.0F;
        }
    }

    /**
     * Sets the color this lightbar.
     * <p>
     * <b>Shorthand for:</b> {@link #setColor(int, boolean)}, with the
     * argument for {@code rgba} being {@code rgb} and the argument for
     * {@code useAlpha} being {@code false}.
     *
     * @param rgb the RGB color value.
     */
    @SuppressWarnings("InvalidParam")
    public void setColor(int rgb) {
        this.setColor(rgb, false);
    }

    /**
     * Sets the color of the lightbar.
     * <p>
     * <b>Shorthand for:</b> {@link #setColor(int, boolean)}, with the
     * argument for {@code rgba} being {@code color.getRGB()} and the
     * argument {@code useAlpha} being {@code true}.
     *
     * @param color the color value. A value of {@code null} is permitted,
     *              and will have the argument for {@code rgba} passed as
     *              {@code 0x00000000}.
     */
    public void setColor(@Nullable Color color) {
        if (color == null) {
            this.setColor(0x00000000, true);
        } else {
            /* convert ARGB to RGBA for setColor(int) */
            long rgba = (long) color.getRGB() << 8;
            rgba |= ((color.getRGB() & 0xFF000000L) >> 24);
            this.setColor((int) rgba, true);
        }
    }

}
