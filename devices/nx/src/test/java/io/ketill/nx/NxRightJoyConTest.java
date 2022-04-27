package io.ketill.nx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.nx.NxRightJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxRightJoyConTest {

    private NxRightJoyCon nxRightJoyCon;

    private void assertStateIsFeature(Object state, IoFeature<?, ?> feature) {
        assertSame(state, nxRightJoyCon.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.nxRightJoyCon = new NxRightJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(nxRightJoyCon.a, BUTTON_A);
        assertStateIsFeature(nxRightJoyCon.x, BUTTON_X);
        assertStateIsFeature(nxRightJoyCon.b, BUTTON_B);
        assertStateIsFeature(nxRightJoyCon.y, BUTTON_Y);
        assertStateIsFeature(nxRightJoyCon.sl, BUTTON_SL);
        assertStateIsFeature(nxRightJoyCon.sr, BUTTON_SR);
        assertStateIsFeature(nxRightJoyCon.plus, BUTTON_PLUS);
        assertStateIsFeature(nxRightJoyCon.rThumb, BUTTON_R_THUMB);
        assertStateIsFeature(nxRightJoyCon.home, BUTTON_HOME);
        assertStateIsFeature(nxRightJoyCon.r, BUTTON_R);
        assertStateIsFeature(nxRightJoyCon.zr, BUTTON_ZR);

        assertStateIsFeature(nxRightJoyCon.rs, STICK_RS);

        assertStateIsFeature(nxRightJoyCon.rt, TRIGGER_ZR);
    }

    @Test
    void leftJoyCon() {
        assertFalse(nxRightJoyCon.isLeftJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxRightJoyCon::asLeftJoyCon);
    }

    @Test
    void rightJoyCon() {
        assertTrue(nxRightJoyCon.isRightJoyCon());
        assertSame(nxRightJoyCon, nxRightJoyCon.asRightJoyCon());
    }

}