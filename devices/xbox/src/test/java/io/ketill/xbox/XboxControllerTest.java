package io.ketill.xbox;

import io.ketill.IoFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.ketill.xbox.XboxController.*;
import static org.junit.jupiter.api.Assertions.*;

class XboxControllerTest {
    
    private XboxController xbox;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertEquals(state, xbox.getState(feature));
    }
    
    @BeforeEach
    void setup() {
        this.xbox = new XboxController(MockXboxAdapter::new);
    }
    
    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(xbox.a, BUTTON_A);
        assertStateIsFeature(xbox.b, BUTTON_B);
        assertStateIsFeature(xbox.x, BUTTON_X);
        assertStateIsFeature(xbox.y, BUTTON_Y);
        assertStateIsFeature(xbox.lb, BUTTON_LB);
        assertStateIsFeature(xbox.rb, BUTTON_RB);
        assertStateIsFeature(xbox.guide, BUTTON_GUIDE);
        assertStateIsFeature(xbox.start, BUTTON_START);
        assertStateIsFeature(xbox.lThumb, BUTTON_L_THUMB);
        assertStateIsFeature(xbox.rThumb, BUTTON_R_THUMB);
        assertStateIsFeature(xbox.up, BUTTON_UP);
        assertStateIsFeature(xbox.right, BUTTON_RIGHT);
        assertStateIsFeature(xbox.down, BUTTON_DOWN);
        assertStateIsFeature(xbox.left, BUTTON_LEFT);

        assertStateIsFeature(xbox.ls, STICK_LS);
        assertStateIsFeature(xbox.rs, STICK_RS);

        assertStateIsFeature(xbox.lt, TRIGGER_LT);
        assertStateIsFeature(xbox.rt, TRIGGER_RT);

        assertStateIsFeature(xbox.rumbleCoarse, MOTOR_COARSE);
        assertStateIsFeature(xbox.rumbleFine, MOTOR_FINE);
    }

}