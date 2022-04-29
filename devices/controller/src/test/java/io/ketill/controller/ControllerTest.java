package io.ketill.controller;

import io.ketill.Direction;
import io.ketill.pressable.PressableFeatureConfig;
import io.ketill.pressable.PressableFeatureEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

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
    void rumble() {
        /* register rumble motor for next test */
        RumbleMotor motor = new RumbleMotor("rumble");
        MotorVibration vibration =
                controller.registerFeature(motor).containerState;

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

    @Test
    void onDeviceButtonEvent() {
        /* set callback for next test */
        AtomicBoolean notified = new AtomicBoolean();
        controller.onPressableEvent(e -> notified.set(true));

        /*
         * When a device button is registered, the controller should notify
         * listeners when said button is pressed or released.
         */
        DeviceButton button = new DeviceButton("button");
        controller.registerFeature(button);
        ButtonStateZ state = controller.getInternalState(button);

        state.pressed = true; /* press button */
        controller.poll(); /* fire events */
        assertTrue(notified.get());

        /*
         * When a device button is unregistered, the controller should no
         * longer notify listeners when said button is pressed or released.
         */
        controller.unregisterFeature(button);
        notified.set(false);

        state.pressed = false; /* release button */
        controller.poll(); /* fire events */
        assertFalse(notified.get());
    }

    @Test
    void onAnalogStickEvent() {
        /* set callback for next test */
        /* @formatter:off */
        AtomicBoolean
                notifiedUp = new AtomicBoolean(),
                notifiedDown = new AtomicBoolean(),
                notifiedLeft = new AtomicBoolean(),
                notifiedRight = new AtomicBoolean();

        controller.onPressableEvent(e -> {
            if(e.data == null) {
                return;
            }
            switch((Direction) e.data) {
                case UP:
                    notifiedUp.set(true);
                    break;
                case DOWN:
                    notifiedDown.set(true);
                    break;
                case LEFT:
                    notifiedLeft.set(true);
                    break;
                case RIGHT:
                    notifiedRight.set(true);
                    break;
            }
        });
        /* @formatter:on */

        /*
         * When an analog stick is registered, the controller should notify
         * listeners when said it is moved towards a direction.
         */
        AnalogStick stick = new AnalogStick("stick");
        controller.registerFeature(stick);
        StickPosZ pos = controller.getInternalState(stick);

        pos.y = 1.0F; /* press upwards */
        controller.poll(); /* fire events */
        assertTrue(notifiedUp.get());

        pos.y = -1.0F; /* press downwards */
        controller.poll(); /* fire events */
        assertTrue(notifiedDown.get());

        pos.x = -1.0F; /* press towards left */
        controller.poll(); /* fire events */
        assertTrue(notifiedLeft.get());

        pos.x = 1.0F; /* press towards right */
        controller.poll();
        assertTrue(notifiedRight.get());

        /*
         * When an analog stick is unregistered, the controller should no
         * longer notify listeners when it is moved towards a direction.
         */
        controller.unregisterFeature(stick);
        notifiedDown.set(false);
        notifiedRight.set(false);

        pos.y = 0.0F; /* move towards center */
        controller.poll(); /* fire events */
        assertFalse(notifiedDown.get());

        pos.x = 0.0F; /* move towards center */
        controller.poll();
        assertFalse(notifiedRight.get());
    }

    @Test
    void onAnalogTriggerEvent() {
        /* set callback for next test */
        AtomicBoolean notified = new AtomicBoolean();
        controller.onPressableEvent(e -> notified.set(true));

        /*
         * When an analog trigger is registered, the controller is expected
         * to notify listeners when it is pressed or released.
         */
        AnalogTrigger trigger = new AnalogTrigger("trigger");
        controller.registerFeature(trigger);
        TriggerStateZ state = controller.getInternalState(trigger);

        state.force = 1.0F; /* press trigger */
        controller.poll(); /* fire events */
        assertTrue(notified.get());

        /*
         * When an analog trigger unregistered, the controller should no
         * longer notify listeners when it is pressed or released.
         */
        controller.unregisterFeature(trigger);
        notified.set(false);

        state.force = 0.0F; /* release trigger */
        controller.poll(); /* fire events */
        assertFalse(notified.get());
    }

    @Test
    void getPressableCallback() {
        assertNull(controller.getPressableCallback());
    }

    @Test
    void usePressableCallback() {
        /* @formatter:off */
        Consumer<PressableFeatureEvent> callback = (e) -> {};
        controller.onPressableEvent(callback);
        assertSame(callback, controller.getPressableCallback());
        /* @formatter:on */
    }

    @Test
    void usePressableConfig() {
        PressableFeatureConfig config = new PressableFeatureConfig();
        controller.usePressableConfig(config);
        assertSame(config, controller.getPressableConfig());

        /*
         * When the controller is told to use a null value for the pressable
         * feature config, it should use the default configuration instead.
         */
        controller.usePressableConfig(null);
        assertSame(PressableFeatureConfig.DEFAULT,
                controller.getPressableConfig());
    }

    @Test
    void getPressableConfig() {
        assertSame(PressableFeatureConfig.DEFAULT,
                controller.getPressableConfig());
    }

}
