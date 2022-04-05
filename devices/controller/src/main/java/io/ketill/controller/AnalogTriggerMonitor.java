package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureMonitor;
import io.ketill.pressable.PressableFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnalogTriggerMonitor
        extends PressableFeatureMonitor<AnalogTrigger, Trigger1f> {

    /* @formatter:off */
    <I extends IoDevice & PressableFeatureSupport>
            AnalogTriggerMonitor(@NotNull I device,
                                 @NotNull AnalogTrigger trigger,
                                 @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        super(device, trigger, callbackSupplier);
    }
    /* @formatter:on */

    @Override
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        switch (event.type) {
            case PRESS:
                state.button.pressed = true;
                break;
            case HOLD:
                state.button.held = true;
                break;
            case RELEASE:
                state.button.pressed = false;
                state.button.held = false;
                break;
        }
    }

    @Override
    protected boolean isPressed() {
        return AnalogTrigger.isPressed(state.getForce());
    }

}
