package io.ketill.gc;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.gc.GcController.*;
import static org.junit.jupiter.api.Assertions.*;

class GcControllerTest {

    private GcController gc;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, gc.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.gc = new GcController(MockGcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(gc.a, BUTTON_A);
        assertStateIsFeature(gc.b, BUTTON_B);
        assertStateIsFeature(gc.x, BUTTON_X);
        assertStateIsFeature(gc.y, BUTTON_Y);
        assertStateIsFeature(gc.left, BUTTON_LEFT);
        assertStateIsFeature(gc.right, BUTTON_RIGHT);
        assertStateIsFeature(gc.down, BUTTON_DOWN);
        assertStateIsFeature(gc.up, BUTTON_UP);
        assertStateIsFeature(gc.start, BUTTON_START);
        assertStateIsFeature(gc.z, BUTTON_Z);
        assertStateIsFeature(gc.r, BUTTON_R);
        assertStateIsFeature(gc.l, BUTTON_L);

        assertStateIsFeature(gc.ls, STICK_LS);
        assertStateIsFeature(gc.rs, STICK_RS);

        assertStateIsFeature(gc.lt, TRIGGER_LT);
        assertStateIsFeature(gc.rt, TRIGGER_RT);

        assertStateIsFeature(gc.rumble, MOTOR_RUMBLE);
    }

}