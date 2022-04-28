package io.ketill.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.psx.Ps5Controller.*;

class Ps5ControllerTest {

    private Ps5Controller ps5;

    @BeforeEach
    void setup() {
        this.ps5 = new Ps5Controller(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(ps5, ps5.share, BUTTON_SHARE);
        assertFeatureOwnsState(ps5, ps5.options, BUTTON_OPTIONS);
        assertFeatureOwnsState(ps5, ps5.ps, BUTTON_PS);
        assertFeatureOwnsState(ps5, ps5.tpad, BUTTON_TPAD);
        assertFeatureOwnsState(ps5, ps5.mute, BUTTON_MUTE);

        assertFeatureOwnsState(ps5, ps5.lt, TRIGGER_LT);
        assertFeatureOwnsState(ps5, ps5.rt, TRIGGER_RT);
    }

}