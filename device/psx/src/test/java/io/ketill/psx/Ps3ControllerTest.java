package io.ketill.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.psx.Ps3Controller.*;

class Ps3ControllerTest {

    private Ps3Controller ps3;

    @BeforeEach
    void createController() {
        this.ps3 = new Ps3Controller(MockPsxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(ps3, ps3.select, BUTTON_SELECT);
        assertFeatureOwnsState(ps3, ps3.start, BUTTON_START);

        assertFeatureOwnsState(ps3, ps3.lt, TRIGGER_LT);
        assertFeatureOwnsState(ps3, ps3.rt, TRIGGER_RT);

        assertFeatureOwnsState(ps3, ps3.accelerometer, SENSOR_ACCELEROMETER);
        assertFeatureOwnsState(ps3, ps3.gyroscope, SENSOR_GYROSCOPE);

        assertFeatureOwnsState(ps3, ps3.battery, INTERNAL_BATTERY);

        assertFeatureOwnsState(ps3, ps3.rumbleStrong, MOTOR_STRONG);
        assertFeatureOwnsState(ps3, ps3.rumbleWeak, MOTOR_WEAK);

        assertFeatureOwnsState(ps3, ps3.led, FEATURE_LED);
    }

}