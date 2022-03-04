package io.ketill.nx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.nx.NxLeftJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxLeftJoyConTest {

    private NxLeftJoyCon nxLeft;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertEquals(state, nxLeft.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.nxLeft = new NxLeftJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(nxLeft.left, BUTTON_LEFT);
        assertStateIsFeature(nxLeft.down, BUTTON_DOWN);
        assertStateIsFeature(nxLeft.up, BUTTON_UP);
        assertStateIsFeature(nxLeft.right, BUTTON_RIGHT);
        assertStateIsFeature(nxLeft.sl, BUTTON_SL);
        assertStateIsFeature(nxLeft.sr, BUTTON_SR);
        assertStateIsFeature(nxLeft.minus, BUTTON_MINUS);
        assertStateIsFeature(nxLeft.lThumb, BUTTON_L_THUMB);
        assertStateIsFeature(nxLeft.snapshot, BUTTON_SNAPSHOT);
        assertStateIsFeature(nxLeft.l, BUTTON_L);
        assertStateIsFeature(nxLeft.zl, BUTTON_ZL);

        assertStateIsFeature(nxLeft.ls, STICK_LS);

        assertStateIsFeature(nxLeft.lt, TRIGGER_ZL);

        assertStateIsFeature(nxLeft.led, FEATURE_LED);
    }

    @Test
    void leftJoyCon() {
        assertTrue(nxLeft.isLeftJoyCon());
        assertEquals(nxLeft, nxLeft.asLeftJoyCon());
    }

    @Test
    void rightJoyCon() {
        assertFalse(nxLeft.isRightJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxLeft::asRightJoyCon);
    }

}
