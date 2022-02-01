package com.whirvis.ketill.gc;

import com.whirvis.ketill.Button1b;
import com.whirvis.ketill.DeviceAdapter;
import com.whirvis.ketill.DeviceButton;
import com.whirvis.ketill.FeatureAdapter;
import com.whirvis.ketill.MappedFeatureRegistry;
import com.whirvis.ketill.Trigger1f;
import com.whirvis.ketill.Vibration1f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import static com.whirvis.ketill.gc.GcController.*;

/**
 * A USB GameCube adapter for a Nintendo GameCube controller.
 *
 * @see GcUsbDevice
 */
public class GcUsbAdapter extends DeviceAdapter<GcController> {

    /* @formatter:off */
    public static final GcStickMapping
            MAPPING_LS = new GcStickMapping(0, 1, 34, 230, 30, 232),
            MAPPING_RS = new GcStickMapping(2, 3, 48, 226, 30, 218);

    public static final GcTriggerMapping
            MAPPING_LT = new GcTriggerMapping(4, 42, 186),
            MAPPING_RT = new GcTriggerMapping(5, 42, 186);
    /* @formatter: on */

    private static final int BUTTON_COUNT = 12;
    private static final int ANALOG_COUNT = 6;

    private @Nullable GcController controller;

    private final GcUsbDevice device;
    private final int slot;
    private int type;
    private final boolean[] buttons;
    private final int[] analogs;
    private boolean rumbling;

    /**
     * @param device the USB adapter this controller belongs to.
     * @param slot   the controller slot.
     */
    protected GcUsbAdapter(@NotNull GcUsbDevice device, int slot) {
        this.device = device;
        this.slot = slot;
        this.buttons = new boolean[BUTTON_COUNT];
        this.analogs = new int[ANALOG_COUNT];
    }

    private float getNormal(int gcAxis, int min, int max) {
        int pos = analogs[gcAxis];

        /*
         * It's not uncommon for an axis to go one or two points outside
         * usual minimum or maximum values. Clamping them to will prevent
         * return values outside the -1.0F to 1.0F range.
         */
        if (pos < min) {
            pos = min;
        } else if (pos > max) {
            pos = max;
        }

        float mid = (max - min) / 2.0F;
        return (pos - min - mid) / mid;
    }

    public boolean isPortConnected() {
        return this.type > 0;
    }

    @Override
    public boolean isDeviceConnected(@NotNull GcController controller) {
        return this.isPortConnected();
    }

    @FeatureAdapter
    public void updateButton(Button1b button, int gcButton) {
        button.pressed = this.buttons[gcButton];
    }

    @FeatureAdapter
    public void updateStick(Vector3f stick, GcStickMapping mapping) {
        stick.x = this.getNormal(mapping.gcAxisX, mapping.xMin, mapping.xMax);
        stick.y = this.getNormal(mapping.gcAxisY, mapping.yMin, mapping.yMax);
    }

    @FeatureAdapter
    public void updateTrigger(Trigger1f trigger, GcTriggerMapping mapping) {
        float pos = this.getNormal(mapping.gcAxis, mapping.min, mapping.max);
        trigger.force = (pos + 1.0F) / 2.0F;
    }

    @FeatureAdapter
    public void updateRumble(Vibration1f motor) {
        this.rumbling = motor.force > 0;
    }

    public boolean isRumbling() {
        if(controller == null) {
            return false;
        }

        /*
         * It is possible that the program disconnects from the adapter while
         * the controller should still be rumbling. Checking if the controller
         * is connected is an easy to tell if it should stop rumbling.
         */
        return this.isDeviceConnected(controller) && this.rumbling;
    }

    private void mapButton(@NotNull MappedFeatureRegistry registry,
                           @NotNull DeviceButton button, int gcButton) {
        registry.mapFeature(button, gcButton, this::updateButton);
    }

    @Override
    protected void initAdapter(@NotNull GcController controller,
                               @NotNull MappedFeatureRegistry registry) {
        this.controller = controller;

        this.mapButton(registry, BUTTON_A, 0);
        this.mapButton(registry, BUTTON_B, 1);
        this.mapButton(registry, BUTTON_X, 2);
        this.mapButton(registry, BUTTON_Y, 3);
        this.mapButton(registry, BUTTON_LEFT, 4);
        this.mapButton(registry, BUTTON_RIGHT, 5);
        this.mapButton(registry, BUTTON_DOWN, 6);
        this.mapButton(registry, BUTTON_UP, 7);
        this.mapButton(registry, BUTTON_START, 8);
        this.mapButton(registry, BUTTON_Z, 9);
        this.mapButton(registry, BUTTON_R, 10);
        this.mapButton(registry, BUTTON_L, 11);

        registry.mapFeature(STICK_LS, MAPPING_LS, this::updateStick);
        registry.mapFeature(STICK_RS, MAPPING_RS, this::updateStick);

        registry.mapFeature(TRIGGER_LT, MAPPING_LT, this::updateTrigger);
        registry.mapFeature(TRIGGER_RT, MAPPING_RT, this::updateTrigger);

        registry.mapFeature(MOTOR_RUMBLE, this::updateRumble);

    }

    @Override
    public void pollDevice(@NotNull GcController c) {
        byte[] data = device.getSlotData(slot);
        int offset = 0;

        /*
         * The first byte is the current controller type. This will be used to
         * determine if the controller is connected, and regardless of whether
         * it self-reports to be wireless. Wireless controllers are usually
         * Wavebird controllers. However, there is no guarantee for this.
         */
        this.type = (data[offset++] & 0xFF) >> 4;

        /*
         * The next two bytes of the data payload are the button states. Each
         * button state is stored in a single bit for the next two bytes. As
         * such, bit shifting is required to determine if a button is pressed.
         */
        short buttonStates = 0;
        buttonStates |= (data[offset++] & 0xFF);
        buttonStates |= (data[offset++] & 0xFF) << 8;
        for (int i = 0; i < buttons.length; i++) {
            this.buttons[i] = (buttonStates & (1 << i)) != 0;
        }

        /*
         * Each analog axis value is stored in a single byte. The amount of
         * bytes that are read here is determined by the amount of axes there
         * are on the controller. For the GameCube controller, it is six.
         */
        for (int i = 0; i < analogs.length; i++) {
            this.analogs[i] = data[offset++] & 0xFF;
        }
    }

    public void poll() {
        if(controller != null) {
            this.pollDevice(controller);
        }
    }

}
