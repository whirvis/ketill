package io.ketill.psx;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.psx.Ps3Controller.*;
import static org.junit.jupiter.api.Assertions.*;

class Ps3ControllerTest {

    private Ps3Controller ps3;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, ps3.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.ps3 = new Ps3Controller(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(ps3.select, BUTTON_SELECT);
        assertStateIsFeature(ps3.start, BUTTON_START);

        assertStateIsFeature(ps3.lt, TRIGGER_LT);
        assertStateIsFeature(ps3.rt, TRIGGER_RT);

        assertStateIsFeature(ps3.rumbleStrong, MOTOR_STRONG);
        assertStateIsFeature(ps3.rumbleWeak, MOTOR_WEAK);

        assertStateIsFeature(ps3.led, FEATURE_LED);
    }

}