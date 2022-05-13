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
        extends PressableFeatureMonitor<AnalogTrigger, TriggerStateZ> {

    /* @formatter:off */
    <I extends IoDevice & PressableFeatureSupport>
            AnalogTriggerMonitor(@NotNull I device,
                                 @NotNull AnalogTrigger trigger,
                                 @NotNull TriggerStateZ state,
                                 @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        super(device, trigger, state, callbackSupplier);
    }
    /* @formatter:on */

    @Override
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        switch (event.type) {
            case PRESS:
                internalState.pressed = true;
                break;
            case HOLD:
                internalState.held = true;
                break;
            case RELEASE:
                internalState.pressed = false;
                internalState.held = false;
                break;
        }
    }

    @Override
    protected boolean isPressed() {
        return AnalogTrigger.isPressed(internalState.calibratedForce);
    }

}
