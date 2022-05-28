package io.ketill.controller;

import io.ketill.pressable.PressableIoFeatureConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    private AnalogStick ls;
    private AnalogTrigger lt;
    private MockController controller;

    @BeforeEach
    void createController() {
        this.ls = new AnalogStick("ls");
        this.lt = new AnalogTrigger("lt");
        this.controller = new MockController(MockControllerAdapter::new,
                ls, null, lt, null);
    }

    @Test
    void ensureProvidedFeaturesMatch() {
        /*
         * Analog sticks and triggers provided at construction should be
         * automatically registered and their state accessible via fields.
         */
        assertSame(controller.ls, controller.getState(ls));
        assertSame(controller.lt, controller.getState(lt));

        /*
         * If one such a feature is not provided at construction, the field
         * storing their state should be null.
         */
        assertNull(controller.rs);
        assertNull(controller.rt);
    }

    @Test
    void testGetPressableConfig() {
        assertSame(PressableIoFeatureConfig.DEFAULT,
                controller.getPressableConfig());
    }

    @Test
    void testUsePressableConfig() {
        PressableIoFeatureConfig config = new PressableIoFeatureConfig();
        controller.usePressableConfig(config);
        assertSame(config, controller.getPressableConfig());

        /*
         * When the controller is told to use a null value for the pressable
         * feature config, it should use the default configuration instead.
         */
        controller.usePressableConfig(null);
        assertSame(PressableIoFeatureConfig.DEFAULT,
                controller.getPressableConfig());
    }

    @Test
    void testRumble() {
        /* register rumble motor for next test */
        RumbleMotor motor = new RumbleMotor("rumble");
        MotorVibration vibration =
                controller.registerFeature(motor).getState();

        /*
         * When using the rumble() function, the controller should set the
         * vibration force of all rumble motors currently registered.
         */
        float force = new Random().nextFloat();
        controller.rumble(force);
        assertEquals(force, vibration.getStrength());

        /*
         * After unregistering the rumble motor, its vibration force should
         * not be updated on the next call to the rumble() method.
         */
        controller.unregisterFeature(motor);
        controller.rumble(0.5F);
        assertEquals(0.0F, vibration.getStrength());
    }

}
