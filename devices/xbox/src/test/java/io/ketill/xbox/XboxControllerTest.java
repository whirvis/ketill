package io.ketill.xbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.KetillAssertions.*;
import static io.ketill.xbox.XboxController.*;

class XboxControllerTest {
    
    private XboxController xbox;
    
    @BeforeEach
    void createController() {
        this.xbox = new XboxController(MockXboxAdapter::new);
    }
    
    @Test
    void ensureAllStatesValid() {
        assertFeatureOwnsState(xbox, xbox.a, BUTTON_A);
        assertFeatureOwnsState(xbox, xbox.b, BUTTON_B);
        assertFeatureOwnsState(xbox, xbox.x, BUTTON_X);
        assertFeatureOwnsState(xbox, xbox.y, BUTTON_Y);
        assertFeatureOwnsState(xbox, xbox.lb, BUTTON_LB);
        assertFeatureOwnsState(xbox, xbox.rb, BUTTON_RB);
        assertFeatureOwnsState(xbox, xbox.guide, BUTTON_GUIDE);
        assertFeatureOwnsState(xbox, xbox.start, BUTTON_START);
        assertFeatureOwnsState(xbox, xbox.lThumb, BUTTON_L_THUMB);
        assertFeatureOwnsState(xbox, xbox.rThumb, BUTTON_R_THUMB);
        assertFeatureOwnsState(xbox, xbox.up, BUTTON_UP);
        assertFeatureOwnsState(xbox, xbox.right, BUTTON_RIGHT);
        assertFeatureOwnsState(xbox, xbox.down, BUTTON_DOWN);
        assertFeatureOwnsState(xbox, xbox.left, BUTTON_LEFT);

        assertFeatureOwnsState(xbox, xbox.ls, STICK_LS);
        assertFeatureOwnsState(xbox, xbox.rs, STICK_RS);

        assertFeatureOwnsState(xbox, xbox.lt, TRIGGER_LT);
        assertFeatureOwnsState(xbox, xbox.rt, TRIGGER_RT);

        assertFeatureOwnsState(xbox, xbox.rumbleCoarse, MOTOR_COARSE);
        assertFeatureOwnsState(xbox, xbox.rumbleFine, MOTOR_FINE);
    }

}