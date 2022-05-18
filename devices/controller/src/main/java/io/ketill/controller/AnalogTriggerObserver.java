package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableIoFeatureObserver;
import io.ketill.pressable.PressableIoFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

final class AnalogTriggerMonitor
        extends PressableIoFeatureObserver<AnalogTrigger, TriggerStateZ> {

    /* @formatter:off */
    <I extends IoDevice & PressableIoFeatureSupport>
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
