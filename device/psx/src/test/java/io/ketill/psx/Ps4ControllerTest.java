package io.ketill.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.psx.Ps4Controller.*;

class Ps4ControllerTest {

    private Ps4Controller ps4;

    @BeforeEach
    void createController() {
        this.ps4 = new Ps4Controller(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(ps4, ps4.share, BUTTON_SHARE);
        assertFeatureOwnsState(ps4, ps4.options, BUTTON_OPTIONS);
        assertFeatureOwnsState(ps4, ps4.ps, BUTTON_PS);
        assertFeatureOwnsState(ps4, ps4.tpad, BUTTON_TPAD);

        assertFeatureOwnsState(ps4, ps4.lt, TRIGGER_LT);
        assertFeatureOwnsState(ps4, ps4.rt, TRIGGER_RT);

        assertFeatureOwnsState(ps4, ps4.accelerometer, SENSOR_ACCELEROMETER);
        assertFeatureOwnsState(ps4, ps4.gyroscope, SENSOR_GYROSCOPE);

        assertFeatureOwnsState(ps4, ps4.battery, INTERNAL_BATTERY);

        assertFeatureOwnsState(ps4, ps4.rumbleStrong, MOTOR_STRONG);
        assertFeatureOwnsState(ps4, ps4.rumbleWeak, MOTOR_WEAK);

        assertFeatureOwnsState(ps4, ps4.lightbar, FEATURE_LIGHTBAR);
    }

}