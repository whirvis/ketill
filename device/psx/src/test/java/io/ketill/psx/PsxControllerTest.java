package io.ketill.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.psx.PsxController.*;
import static org.junit.jupiter.api.Assertions.*;

class PsxControllerTest {

    private PsxController psx;

    @BeforeEach
    void createController() {
        this.psx = new MockPsxController(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(psx, psx.square, BUTTON_SQUARE);
        assertFeatureOwnsState(psx, psx.cross, BUTTON_CROSS);
        assertFeatureOwnsState(psx, psx.circle, BUTTON_CIRCLE);
        assertFeatureOwnsState(psx, psx.triangle, BUTTON_TRIANGLE);
        assertFeatureOwnsState(psx, psx.l1, BUTTON_L1);
        assertFeatureOwnsState(psx, psx.r1, BUTTON_R1);
        assertFeatureOwnsState(psx, psx.l2, BUTTON_L2);
        assertFeatureOwnsState(psx, psx.r2, BUTTON_R2);
        assertFeatureOwnsState(psx, psx.lThumb, BUTTON_L_THUMB);
        assertFeatureOwnsState(psx, psx.rThumb, BUTTON_R_THUMB);
        assertFeatureOwnsState(psx, psx.up, BUTTON_UP);
        assertFeatureOwnsState(psx, psx.right, BUTTON_RIGHT);
        assertFeatureOwnsState(psx, psx.down, BUTTON_DOWN);
        assertFeatureOwnsState(psx, psx.left, BUTTON_LEFT);

        assertNull(psx.lt);
        assertNull(psx.rt);

        assertFeatureOwnsState(psx, psx.ls, STICK_LS);
        assertFeatureOwnsState(psx, psx.rs, STICK_RS);
    }

}