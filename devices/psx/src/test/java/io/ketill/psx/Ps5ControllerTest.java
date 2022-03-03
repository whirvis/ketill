package io.ketill.psx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.psx.Ps5Controller.*;
import static org.junit.jupiter.api.Assertions.*;

class Ps5ControllerTest {

    private Ps5Controller ps5;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertEquals(state, ps5.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.ps5 = new Ps5Controller(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(ps5.share, BUTTON_SHARE);
        assertStateIsFeature(ps5.options, BUTTON_OPTIONS);
        assertStateIsFeature(ps5.ps, BUTTON_PS);
        assertStateIsFeature(ps5.tpad, BUTTON_TPAD);
        assertStateIsFeature(ps5.mute, BUTTON_MUTE);

        assertStateIsFeature(ps5.lt, TRIGGER_LT);
        assertStateIsFeature(ps5.rt, TRIGGER_RT);
    }

}