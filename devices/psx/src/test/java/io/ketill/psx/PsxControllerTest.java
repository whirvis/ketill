package io.ketill.psx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.psx.PsxController.*;
import static org.junit.jupiter.api.Assertions.*;

class PsxControllerTest {

    private PsxController psx;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, psx.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.psx = new MockPsxController(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(psx.square, BUTTON_SQUARE);
        assertStateIsFeature(psx.cross, BUTTON_CROSS);
        assertStateIsFeature(psx.circle, BUTTON_CIRCLE);
        assertStateIsFeature(psx.triangle, BUTTON_TRIANGLE);
        assertStateIsFeature(psx.l1, BUTTON_L1);
        assertStateIsFeature(psx.r1, BUTTON_R1);
        assertStateIsFeature(psx.l2, BUTTON_L2);
        assertStateIsFeature(psx.r2, BUTTON_R2);
        assertStateIsFeature(psx.lThumb, BUTTON_L_THUMB);
        assertStateIsFeature(psx.rThumb, BUTTON_R_THUMB);
        assertStateIsFeature(psx.up, BUTTON_UP);
        assertStateIsFeature(psx.right, BUTTON_RIGHT);
        assertStateIsFeature(psx.down, BUTTON_DOWN);
        assertStateIsFeature(psx.left, BUTTON_LEFT);

        assertNull(psx.lt);
        assertNull(psx.rt);

        assertStateIsFeature(psx.ls, STICK_LS);
        assertStateIsFeature(psx.ls, STICK_RS);
    }

}