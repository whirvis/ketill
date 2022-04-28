package io.ketill.nx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.nx.NxLeftJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxLeftJoyConTest {

    private NxLeftJoyCon nxLeftJoyCon;

    @BeforeEach
    void setup() {
        this.nxLeftJoyCon = new NxLeftJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.left, BUTTON_LEFT);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.down, BUTTON_DOWN);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.up, BUTTON_UP);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.right, BUTTON_RIGHT);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.sl, BUTTON_SL);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.sr, BUTTON_SR);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.minus, BUTTON_MINUS);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.lThumb, BUTTON_L_THUMB);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.snapshot, BUTTON_SNAPSHOT);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.l, BUTTON_L);
        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.zl, BUTTON_ZL);

        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.ls, STICK_LS);

        assertFeatureOwnsState(nxLeftJoyCon, nxLeftJoyCon.lt, TRIGGER_ZL);
    }

    @Test
    void leftJoyCon() {
        assertTrue(nxLeftJoyCon.isLeftJoyCon());
        assertSame(nxLeftJoyCon, nxLeftJoyCon.asLeftJoyCon());
    }

    @Test
    void rightJoyCon() {
        assertFalse(nxLeftJoyCon.isRightJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxLeftJoyCon::asRightJoyCon);
    }

}
