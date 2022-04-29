package io.ketill.psx;

import org.joml.Vector4fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class LightbarColorTest {

    private static final Random RANDOM = new Random();

    private LightbarColor color;
    private Vector4fc vector;

    @BeforeEach
    void setup() {
        this.color = new LightbarColor();
        this.vector = color.getVector();
    }

    @Test
    void setLightbarColor4f() {
        float r = RANDOM.nextFloat();
        float g = RANDOM.nextFloat();
        float b = RANDOM.nextFloat();
        float a = RANDOM.nextFloat();

        /*
         * RANDOM.nextFloat() returns values between within the accepted
         * range of 0.0F to 1.0F. As such, each color channel should equal
         * their generated value exactly.
         */
        color.setColor(r, g, b, a);
        assertEquals(r, vector.x());
        assertEquals(g, vector.y());
        assertEquals(b, vector.z());
        assertEquals(a, vector.w());

        /*
         * When given RGBA values are out of bounds for the lightbar, the
         * lightbar color must clamp them to a range of 0.0F to 1.0F. This
         * is to prevent unexpected behavior from occurring.
         */
        color.setColor(1.23F, 4.56F, 7.89F, 1.01F);
        assertEquals(1.0F, vector.x());
        assertEquals(1.0F, vector.y());
        assertEquals(1.0F, vector.z());
        assertEquals(1.0F, vector.w());

        /* same as previous test, with negative values */
        color.setColor(-1.23F, -4.56F, -7.89F, -1.0F);
        assertEquals(0.0F, vector.x());
        assertEquals(0.0F, vector.y());
        assertEquals(0.0F, vector.z());
        assertEquals(0.0F, vector.w());

        /*
         * When setting the lightbar color with the alpha channel omitted,
         * the lightbar color should default its value to 1.0F. This is to
         * prevent unexpected behavior from occurring.
         */
        color.setColor(0.0F, 0.0F, 0.0F);
        assertEquals(vector.w(), 1.0F);
    }

    @Test
    void setLightbarColorRgba() {
        int rgba = 0xF3C94F88;

        /*
         * This method requires some division and bit-shifting (uh oh, very
         * spooky). This test ensures the math required to convert an RGBA
         * integer to the proper 0.0F to 1.0F floating values is correct.
         */
        color.setColor(rgba, true);
        assertEquals(0xF3 / 255.0F, vector.x());
        assertEquals(0xC9 / 255.0F, vector.y());
        assertEquals(0x4F / 255.0F, vector.z());
        assertEquals(0x88 / 255.0F, vector.w());

        /*
         * Same as the above test, however this ensures the alpha channel is
         * properly discarded and set to 1.0F. This is to prevent unexpected
         * behavior from occurring when the controller is next polled.
         */
        color.setColor(rgba);
        assertEquals(0xF3 / 255.0F, vector.x());
        assertEquals(0xC9 / 255.0F, vector.y());
        assertEquals(0x4F / 255.0F, vector.z());
        assertEquals(1.0F, vector.w());
    }

    @Test
    void setLightbarColorAwt() {
        int r = RANDOM.nextInt(0x100);
        int g = RANDOM.nextInt(0x100);
        int b = RANDOM.nextInt(0x100);
        int a = RANDOM.nextInt(0x100);

        /*
         * Java's built-in Color class stores its RGB pixels in ARGB format.
         * As such, some conversion is required to  convert it to RGBA. This
         * test ensures the conversion is done correctly.
         */
        color.setColor(new Color(r, g, b, a));
        assertEquals(r / 255.0F, vector.x());
        assertEquals(g / 255.0F, vector.y());
        assertEquals(b / 255.0F, vector.z());
        assertEquals(a / 255.0F, vector.w());

        /*
         * When using a Java's built-in Color class, null is permitted as a
         * shorthand for no color. When used, the value of all color channels
         * should be set to 0.0F. This test must be run after the first test
         * to ensure the values were not already set to zero.
         */
        color.setColor(null);
        assertEquals(0.0F, vector.x());
        assertEquals(0.0F, vector.y());
        assertEquals(0.0F, vector.z());
        assertEquals(0.0F, vector.w());
    }

}
