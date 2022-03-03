package io.ketill.nx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.nx.NxRightJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxRightJoyConTest {

    private NxRightJoyCon nxRight;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertEquals(state, nxRight.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.nxRight = new NxRightJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(nxRight.a, BUTTON_A);
        assertStateIsFeature(nxRight.x, BUTTON_X);
        assertStateIsFeature(nxRight.b, BUTTON_B);
        assertStateIsFeature(nxRight.y, BUTTON_Y);
        assertStateIsFeature(nxRight.sl, BUTTON_SL);
        assertStateIsFeature(nxRight.sr, BUTTON_SR);
        assertStateIsFeature(nxRight.plus, BUTTON_PLUS);
        assertStateIsFeature(nxRight.rThumb, BUTTON_R_THUMB);
        assertStateIsFeature(nxRight.home, BUTTON_HOME);
        assertStateIsFeature(nxRight.r, BUTTON_R);
        assertStateIsFeature(nxRight.zr, BUTTON_ZR);

        assertStateIsFeature(nxRight.rs, STICK_RS);

        assertStateIsFeature(nxRight.rt, TRIGGER_ZR);
    }

}