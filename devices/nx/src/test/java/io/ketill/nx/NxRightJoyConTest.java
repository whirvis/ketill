package io.ketill.nx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.nx.NxRightJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxRightJoyConTest {

    private NxRightJoyCon nxRightJoyCon;

    @BeforeEach
    void setup() {
        this.nxRightJoyCon = new NxRightJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.a, BUTTON_A);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.x, BUTTON_X);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.b, BUTTON_B);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.y, BUTTON_Y);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.sl, BUTTON_SL);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.sr, BUTTON_SR);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.plus, BUTTON_PLUS);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.rThumb, BUTTON_R_THUMB);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.home, BUTTON_HOME);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.r, BUTTON_R);
        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.zr, BUTTON_ZR);

        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.rs, STICK_RS);

        assertFeatureOwnsState(nxRightJoyCon, nxRightJoyCon.rt, TRIGGER_ZR);
    }

    @Test
    void ensureIsNotLeftJoyCon() {
        assertFalse(nxRightJoyCon.isLeftJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxRightJoyCon::asLeftJoyCon);
    }

    @Test
    void ensureIsRightJoyCon() {
        assertTrue(nxRightJoyCon.isRightJoyCon());
        assertSame(nxRightJoyCon, nxRightJoyCon.asRightJoyCon());
    }

}