package io.ketill.nx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.nx.NxJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxJoyConTest {

    private MockNxJoyCon nxJoyCon;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, nxJoyCon.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.nxJoyCon = new MockNxJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(nxJoyCon.sl, BUTTON_SL);
        assertStateIsFeature(nxJoyCon.sr, BUTTON_SR);

        assertStateIsFeature(nxJoyCon.led, FEATURE_LED);
    }

    @Test
    void leftJoyCon() {
        assertFalse(nxJoyCon.isLeftJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxJoyCon::asLeftJoyCon);
    }

    @Test
    void rightJoyCon() {
        assertFalse(nxJoyCon.isRightJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxJoyCon::asRightJoyCon);
    }

}
