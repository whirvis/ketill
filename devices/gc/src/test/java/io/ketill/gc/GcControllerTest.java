package io.ketill.gc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.gc.GcController.*;

class GcControllerTest {

    private GcController gc;

    @BeforeEach
    void setup() {
        this.gc = new GcController(MockGcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(gc, gc.a, BUTTON_A);
        assertFeatureOwnsState(gc, gc.b, BUTTON_B);
        assertFeatureOwnsState(gc, gc.x, BUTTON_X);
        assertFeatureOwnsState(gc, gc.y, BUTTON_Y);
        assertFeatureOwnsState(gc, gc.left, BUTTON_LEFT);
        assertFeatureOwnsState(gc, gc.right, BUTTON_RIGHT);
        assertFeatureOwnsState(gc, gc.down, BUTTON_DOWN);
        assertFeatureOwnsState(gc, gc.up, BUTTON_UP);
        assertFeatureOwnsState(gc, gc.start, BUTTON_START);
        assertFeatureOwnsState(gc, gc.z, BUTTON_Z);
        assertFeatureOwnsState(gc, gc.r, BUTTON_R);
        assertFeatureOwnsState(gc, gc.l, BUTTON_L);

        assertFeatureOwnsState(gc, gc.ls, STICK_LS);
        assertFeatureOwnsState(gc, gc.rs, STICK_RS);

        assertFeatureOwnsState(gc, gc.lt, TRIGGER_LT);
        assertFeatureOwnsState(gc, gc.rt, TRIGGER_RT);

        assertFeatureOwnsState(gc, gc.rumble, MOTOR_RUMBLE);
    }

}