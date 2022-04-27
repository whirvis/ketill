package io.ketill.hidusb.gc;

import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.controller.AnalogStick;
import io.ketill.controller.AnalogTrigger;
import io.ketill.controller.ButtonStateZ;
import io.ketill.controller.DeviceButton;
import io.ketill.controller.MotorVibration;
import io.ketill.controller.StickPosZ;
import io.ketill.controller.TriggerStateZ;
import io.ketill.gc.GcController;
import org.jetbrains.annotations.NotNull;

import static io.ketill.gc.GcController.*;

public final class LibUsbGcAdapter extends IoDeviceAdapter<GcController> {

    /* @formatter:off */
    private static final StickMapping
            MAPPING_LS = new StickMapping(0, 1, 34, 230, 30, 232),
            MAPPING_RS = new StickMapping(2, 3, 48, 226, 30, 218);

    private static final AxisMapping
            MAPPING_LT = new AxisMapping(4, 42, 186),
            MAPPING_RT = new AxisMapping(5, 42, 186);
    /* @formatter:on */

    private final GcWiiUSlotState slot;

    LibUsbGcAdapter(@NotNull GcController controller,
                    @NotNull MappedFeatureRegistry registry,
                    @NotNull GcWiiUSlotState slot) {
        super(controller, registry);
        this.slot = slot;
    }

    private float getNormalizedAxis(@NotNull AxisMapping mapping) {
        int pos = slot.getAxis(mapping.gcAxis);

        /*
         * It's not uncommon for an axis to go one or two points
         * outside usual minimum or maximum values. Clamping them
         * will prevent return values outside -1.0F to 1.0F.
         */
        if (pos < mapping.min) {
            pos = mapping.min;
        } else if (pos > mapping.max) {
            pos = mapping.max;
        }

        float mid = (mapping.max - mapping.min) / 2.0F;
        return (pos - mapping.min - mid) / mid;
    }

    @MappingMethod
    private void mapButton(@NotNull DeviceButton button, int gcButton) {
        registry.mapFeature(button, gcButton, this::updateButton);
    }

    @MappingMethod
    private void mapStick(@NotNull AnalogStick stick,
                          @NotNull StickMapping mapping) {
        registry.mapFeature(stick, mapping, this::updateStick);
    }

    @MappingMethod
    private void mapTrigger(@NotNull AnalogTrigger trigger,
                            @NotNull AxisMapping mapping) {
        registry.mapFeature(trigger, mapping, this::updateTrigger);
    }

    @Override
    protected void initAdapter() {
        this.mapButton(BUTTON_A, 0);
        this.mapButton(BUTTON_B, 1);
        this.mapButton(BUTTON_X, 2);
        this.mapButton(BUTTON_Y, 3);
        this.mapButton(BUTTON_LEFT, 4);
        this.mapButton(BUTTON_RIGHT, 5);
        this.mapButton(BUTTON_DOWN, 6);
        this.mapButton(BUTTON_UP, 7);
        this.mapButton(BUTTON_START, 8);
        this.mapButton(BUTTON_Z, 9);
        this.mapButton(BUTTON_R, 10);
        this.mapButton(BUTTON_L, 11);

        this.mapStick(STICK_LS, MAPPING_LS);
        this.mapStick(STICK_RS, MAPPING_RS);

        this.mapTrigger(TRIGGER_LT, MAPPING_LT);
        this.mapTrigger(TRIGGER_RT, MAPPING_RT);

        registry.mapFeature(MOTOR_RUMBLE, this::updateMotor);
    }

    @FeatureAdapter
    private void updateButton(@NotNull ButtonStateZ state, int gcButton) {
        state.pressed = slot.isPressed(gcButton);
    }

    @FeatureAdapter
    private void updateStick(@NotNull StickPosZ pos,
                             @NotNull StickMapping mapping) {
        pos.x = this.getNormalizedAxis(mapping.xAxis);
        pos.y = this.getNormalizedAxis(mapping.yAxis);
    }

    @FeatureAdapter
    private void updateTrigger(@NotNull TriggerStateZ state,
                               @NotNull AxisMapping mapping) {
        state.force = (this.getNormalizedAxis(mapping) + 1.0F) / 2.0F;
    }

    @FeatureAdapter
    private void updateMotor(@NotNull MotorVibration vibration) {
        slot.setRumbling(vibration.getStrength() > 0.0F);
    }

    @Override
    protected void pollDevice() {
        slot.poll();
    }

    @Override
    protected boolean isDeviceConnected() {
        return slot.isConnected();
    }

}
