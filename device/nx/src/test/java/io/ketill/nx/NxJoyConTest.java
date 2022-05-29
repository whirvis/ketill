package io.ketill.nx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.nx.NxJoyCon.*;
import static org.junit.jupiter.api.Assertions.*;

class NxJoyConTest {

    private MockNxJoyCon nxJoyCon;

    @BeforeEach
    void createJoyCon() {
        this.nxJoyCon = new MockNxJoyCon(MockNxAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(nxJoyCon, nxJoyCon.sl, BUTTON_SL);
        assertFeatureOwnsState(nxJoyCon, nxJoyCon.sr, BUTTON_SR);

        assertFeatureOwnsState(nxJoyCon, nxJoyCon.accelerometer, SENSOR_ACCELEROMETER);
        assertFeatureOwnsState(nxJoyCon, nxJoyCon.gyroscope, SENSOR_GYROSCOPE);

        assertFeatureOwnsState(nxJoyCon, nxJoyCon.battery, INTERNAL_BATTERY);

        assertFeatureOwnsState(nxJoyCon, nxJoyCon.led, FEATURE_LED);
    }

    @Test
    void ensureIsNotLeftJoyCon() {
        assertFalse(nxJoyCon.isLeftJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxJoyCon::asLeftJoyCon);
    }

    @Test
    void ensureIsNotRightJoyCon() {
        assertFalse(nxJoyCon.isRightJoyCon());
        assertThrows(UnsupportedOperationException.class,
                nxJoyCon::asRightJoyCon);
    }

}
