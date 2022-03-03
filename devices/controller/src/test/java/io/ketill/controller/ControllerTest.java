package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private AnalogStick ls;
    private AnalogTrigger lt;
    private MockController controller;

    @BeforeEach
    void setup() {
        this.ls = new AnalogStick("ls");
        this.lt = new AnalogTrigger("lt");
        this.controller = new MockController(MockControllerAdapter::new,
                ls, null, lt, null);
    }

    @Test
    void providedFeatures() {
        /*
         * When analog sticks or analog triggers are provided at
         * construction, the controller should register them and
         * store their state as accessible fields.
         */
        assertEquals(controller.ls, controller.getState(ls));
        assertEquals(controller.lt, controller.getState(lt));

        /*
         * If one of the features in question is not provided
         * during construction, their state should be null.
         */
        assertNull(controller.rs);
        assertNull(controller.rt);
    }

    @Test
    void rumble() {
        /* register rumble motor for next test */
        RumbleMotor motor = new RumbleMotor("rumble");
        Vibration1f vibration = controller.registerFeature(motor).state;

        /*
         * When using the rumble() function, the controller should
         * set the vibration force of all rumble motors currently
         * registered to the controller.
         */
        float force = new Random().nextFloat();
        controller.rumble(force);
        assertEquals(force, vibration.force);

        /*
         * The controller should clamp vibration forces of rumble
         * motors within 0.0F to 1.0F when this method is used.
         * This is to prevent possible unexpected behavior.
         */
        controller.rumble(2.0F);
        assertEquals(1.0F, vibration.force);
        controller.rumble(-1.0F);
        assertEquals(0.0F, vibration.force);

        /*
         * After unregistering the rumble motor, its vibration
         * force should not be updated on the next call to the
         * rumble() method. It would not make sense to do so.
         */
        controller.unregisterFeature(motor);
        controller.rumble(0.5F);
        assertEquals(0.0F, vibration.force);
    }

}
