package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureMonitor;
import io.ketill.pressable.PressableFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

final class DeviceButtonMonitor
        extends PressableFeatureMonitor<DeviceButton, ButtonStateZ> {

    /* @formatter:off */
    <I extends IoDevice & PressableFeatureSupport>
            DeviceButtonMonitor(@NotNull I device,
                                @NotNull DeviceButton button,
                                @NotNull ButtonStateZ state,
                                @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        super(device, button, state, callbackSupplier);
    }
    /* @formatter:on */

    @Override
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        switch (event.type) {
            case HOLD:
                internalState.held = true;
                break;
            case RELEASE:
                internalState.held = false;
                break;
        }
    }

    @Override
    protected boolean isPressed() {
        return internalState.pressed;
    }

}
