package io.ketill.nx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.nx.NxProController.*;
import static org.junit.jupiter.api.Assertions.*;

class NxProControllerTest {

    private NxProController nxPro;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, nxPro.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.nxPro = new NxProController(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(nxPro.b, BUTTON_B);
        assertStateIsFeature(nxPro.a, BUTTON_A);
        assertStateIsFeature(nxPro.y, BUTTON_Y);
        assertStateIsFeature(nxPro.x, BUTTON_X);
        assertStateIsFeature(nxPro.l, BUTTON_L);
        assertStateIsFeature(nxPro.r, BUTTON_R);
        assertStateIsFeature(nxPro.zl, BUTTON_ZL);
        assertStateIsFeature(nxPro.zr, BUTTON_ZR);
        assertStateIsFeature(nxPro.minus, BUTTON_MINUS);
        assertStateIsFeature(nxPro.plus, BUTTON_PLUS);
        assertStateIsFeature(nxPro.lThumb, BUTTON_L_THUMB);
        assertStateIsFeature(nxPro.rThumb, BUTTON_R_THUMB);
        assertStateIsFeature(nxPro.home, BUTTON_HOME);
        assertStateIsFeature(nxPro.screenshot, BUTTON_SCREENSHOT);
        assertStateIsFeature(nxPro.bumper, BUTTON_BUMPER);
        assertStateIsFeature(nxPro.zBumper, BUTTON_Z_BUMPER);
        assertStateIsFeature(nxPro.up, BUTTON_UP);
        assertStateIsFeature(nxPro.right, BUTTON_RIGHT);
        assertStateIsFeature(nxPro.down, BUTTON_DOWN);
        assertStateIsFeature(nxPro.left, BUTTON_LEFT);

        assertStateIsFeature(nxPro.ls, STICK_LS);
        assertStateIsFeature(nxPro.rs, STICK_RS);

        assertStateIsFeature(nxPro.lt, TRIGGER_LT);
        assertStateIsFeature(nxPro.rt, TRIGGER_RT);

        assertStateIsFeature(nxPro.led, FEATURE_LED);

        assertStateIsFeature(nxPro.calibration, FEATURE_CALIBRATION);
    }

}