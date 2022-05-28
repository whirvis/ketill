package io.ketill.nx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.nx.NxProController.*;
import static org.junit.jupiter.api.Assertions.*;

class NxProControllerTest {

    private NxProController nxPro;

    @BeforeEach
    void createController() {
        this.nxPro = new NxProController(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(nxPro, nxPro.b, BUTTON_B);
        assertFeatureOwnsState(nxPro, nxPro.a, BUTTON_A);
        assertFeatureOwnsState(nxPro, nxPro.y, BUTTON_Y);
        assertFeatureOwnsState(nxPro, nxPro.x, BUTTON_X);
        assertFeatureOwnsState(nxPro, nxPro.l, BUTTON_L);
        assertFeatureOwnsState(nxPro, nxPro.r, BUTTON_R);
        assertFeatureOwnsState(nxPro, nxPro.zl, BUTTON_ZL);
        assertFeatureOwnsState(nxPro, nxPro.zr, BUTTON_ZR);
        assertFeatureOwnsState(nxPro, nxPro.minus, BUTTON_MINUS);
        assertFeatureOwnsState(nxPro, nxPro.plus, BUTTON_PLUS);
        assertFeatureOwnsState(nxPro, nxPro.lThumb, BUTTON_L_THUMB);
        assertFeatureOwnsState(nxPro, nxPro.rThumb, BUTTON_R_THUMB);
        assertFeatureOwnsState(nxPro, nxPro.home, BUTTON_HOME);
        assertFeatureOwnsState(nxPro, nxPro.screenshot, BUTTON_SCREENSHOT);
        assertFeatureOwnsState(nxPro, nxPro.bumper, BUTTON_BUMPER);
        assertFeatureOwnsState(nxPro, nxPro.zBumper, BUTTON_Z_BUMPER);
        assertFeatureOwnsState(nxPro, nxPro.up, BUTTON_UP);
        assertFeatureOwnsState(nxPro, nxPro.right, BUTTON_RIGHT);
        assertFeatureOwnsState(nxPro, nxPro.down, BUTTON_DOWN);
        assertFeatureOwnsState(nxPro, nxPro.left, BUTTON_LEFT);

        assertFeatureOwnsState(nxPro, nxPro.ls, STICK_LS);
        assertFeatureOwnsState(nxPro, nxPro.rs, STICK_RS);

        assertFeatureOwnsState(nxPro, nxPro.lt, TRIGGER_LT);
        assertFeatureOwnsState(nxPro, nxPro.rt, TRIGGER_RT);

        assertFeatureOwnsState(nxPro, nxPro.led, FEATURE_LED);
    }

    @Test
    void ensureBaseCalibrationsSet() {
        assertSame(CALIBRATION, STICK_LS.getBaseCalibration());
        assertSame(CALIBRATION, STICK_RS.getBaseCalibration());
    }

}