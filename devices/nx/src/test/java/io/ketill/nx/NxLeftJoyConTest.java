package io.ketill.nx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.nx.NxLeftJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxLeftJoyConTest {

    private NxLeftJoyCon nxLeftJoyCon;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertEquals(state, nxLeftJoyCon.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.nxLeftJoyCon = new NxLeftJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(nxLeftJoyCon.left, BUTTON_LEFT);
        assertStateIsFeature(nxLeftJoyCon.down, BUTTON_DOWN);
        assertStateIsFeature(nxLeftJoyCon.up, BUTTON_UP);
        assertStateIsFeature(nxLeftJoyCon.right, BUTTON_RIGHT);
        assertStateIsFeature(nxLeftJoyCon.sl, BUTTON_SL);
        assertStateIsFeature(nxLeftJoyCon.sr, BUTTON_SR);
        assertStateIsFeature(nxLeftJoyCon.minus, BUTTON_MINUS);
        assertStateIsFeature(nxLeftJoyCon.lThumb, BUTTON_L_THUMB);
        assertStateIsFeature(nxLeftJoyCon.snapshot, BUTTON_SNAPSHOT);
        assertStateIsFeature(nxLeftJoyCon.l, BUTTON_L);
        assertStateIsFeature(nxLeftJoyCon.zl, BUTTON_ZL);

        assertStateIsFeature(nxLeftJoyCon.ls, STICK_LS);

        assertStateIsFeature(nxLeftJoyCon.lt, TRIGGER_ZL);
    }

    @Test
    void leftJoyCon() {
        assertTrue(nxLeftJoyCon.isLeftJoyCon());
        assertEquals(nxLeftJoyCon, nxLeftJoyCon.asLeftJoyCon());
    }

    @Test
    void rightJoyCon() {
        assertFalse(nxLeftJoyCon.isRightJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxLeftJoyCon::asRightJoyCon);
    }

}
