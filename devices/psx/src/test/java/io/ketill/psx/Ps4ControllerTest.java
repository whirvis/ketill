package io.ketill.psx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Random;

import static io.ketill.psx.Ps4Controller.*;
import static org.junit.jupiter.api.Assertions.*;

class Ps4ControllerTest {

    private static final Random RANDOM = new Random();

    private Ps4Controller ps4;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, ps4.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.ps4 = new Ps4Controller(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(ps4.share, BUTTON_SHARE);
        assertStateIsFeature(ps4.options, BUTTON_OPTIONS);
        assertStateIsFeature(ps4.ps, BUTTON_PS);
        assertStateIsFeature(ps4.tpad, BUTTON_TPAD);

        assertStateIsFeature(ps4.lt, TRIGGER_LT);
        assertStateIsFeature(ps4.rt, TRIGGER_RT);

        assertStateIsFeature(ps4.rumbleStrong, MOTOR_STRONG);
        assertStateIsFeature(ps4.rumbleWeak, MOTOR_WEAK);

        assertStateIsFeature(ps4.lightbar, FEATURE_LIGHTBAR);
    }

    @Test
    void setLightbarColor4f() {
        float r = RANDOM.nextFloat();
        float g = RANDOM.nextFloat();
        float b = RANDOM.nextFloat();
        float a = RANDOM.nextFloat();

        /*
         * RANDOM.nextFloat() returns values between within the
         * accepted range of 0.0F to 1.0F. As such, each color
         * channel should equal their generated value exactly.
         */
        ps4.setLightbarColor(r, g, b, a);
        assertEquals(r, ps4.lightbar.x);
        assertEquals(g, ps4.lightbar.y);
        assertEquals(b, ps4.lightbar.z);
        assertEquals(a, ps4.lightbar.w);

        /*
         * When given RGBA values out of bounds for the lightbar,
         * the PS4 controller must clamp them to a range of 0.0F
         * to 1.0F. This is to prevent unexpected behavior from
         * occurring when the controller is next polled.
         */
        ps4.setLightbarColor(1.23F, 4.56F, 7.89F, 1.01F);
        assertEquals(1.0F, ps4.lightbar.x);
        assertEquals(1.0F, ps4.lightbar.y);
        assertEquals(1.0F, ps4.lightbar.z);
        assertEquals(1.0F, ps4.lightbar.w);

        /* same as previous test, with negative values */
        ps4.setLightbarColor(-1.23F, -4.56F, -7.89F, -1.0F);
        assertEquals(0.0F, ps4.lightbar.x);
        assertEquals(0.0F, ps4.lightbar.y);
        assertEquals(0.0F, ps4.lightbar.z);
        assertEquals(0.0F, ps4.lightbar.w);

        /*
         * When setting the lightbar color with the alpha channel
         * omitted, the PS4 controller should default its value
         * to 1.0F. This is to prevent unexpected behavior from
         * occurring when the controller is next polled.
         */
        ps4.setLightbarColor(0.0F, 0.0F, 0.0F);
        assertEquals(ps4.lightbar.w, 1.0F);
    }

    @Test
    void setLightbarColorRgba() {
        int rgba = 0xF3C94F88;

        /*
         * This method requires some division and bit-shifting (uh
         * oh, very spooky). This test ensures the math required
         * to convert an RGBA integer to the proper 0.0F to 1.0F
         * floating values is done correctly.
         */
        ps4.setLightbarColor(rgba, true);
        assertEquals(0xF3 / 255.0F, ps4.lightbar.x);
        assertEquals(0xC9 / 255.0F, ps4.lightbar.y);
        assertEquals(0x4F / 255.0F, ps4.lightbar.z);
        assertEquals(0x88 / 255.0F, ps4.lightbar.w);

        /*
         * Same as the above test, however this ensures the alpha
         * channel is properly discarded and set to 1.0F. This is
         * to prevent unexpected behavior from occurring when the
         * controller is next polled.
         */
        ps4.setLightbarColor(rgba);
        assertEquals(0xF3 / 255.0F, ps4.lightbar.x);
        assertEquals(0xC9 / 255.0F, ps4.lightbar.y);
        assertEquals(0x4F / 255.0F, ps4.lightbar.z);
        assertEquals(1.0F, ps4.lightbar.w);
    }

    @Test
    void setLightbarColorAwt() {
        int r = RANDOM.nextInt(0x100);
        int g = RANDOM.nextInt(0x100);
        int b = RANDOM.nextInt(0x100);
        int a = RANDOM.nextInt(0x100);

        /*
         * Java's built-in Color class stores its RGB pixels in
         * ARGB format. As such, some conversion is required to
         * convert it to RGBA. This test ensures the conversion
         * is done correctly.
         */
        ps4.setLightbarColor(new Color(r, g, b, a));
        assertEquals(r / 255.0F, ps4.lightbar.x);
        assertEquals(g / 255.0F, ps4.lightbar.y);
        assertEquals(b / 255.0F, ps4.lightbar.z);
        assertEquals(a / 255.0F, ps4.lightbar.w);

        /*
         * When using a Java's built-in Color class, a null value
         * is permitted as shorthand for no color. When used, the
         * value of all color channels should be set to 0.0F.
         *
         * This test must be run after the first test to ensure
         * the values were not already set to zero.
         */
        ps4.setLightbarColor(null);
        assertEquals(0.0F, ps4.lightbar.x);
        assertEquals(0.0F, ps4.lightbar.y);
        assertEquals(0.0F, ps4.lightbar.z);
        assertEquals(0.0F, ps4.lightbar.w);
    }

}