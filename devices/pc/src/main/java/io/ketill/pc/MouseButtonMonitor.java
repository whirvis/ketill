package io.ketill.pc;

import io.ketill.IoDevice;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableFeatureMonitor;
import io.ketill.pressable.PressableFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

final class MouseButtonMonitor
        extends PressableFeatureMonitor<MouseButton, Click1b> {

    /* @formatter:off */
    <I extends IoDevice & PressableFeatureSupport>
            MouseButtonMonitor(@NotNull I device,
                               @NotNull MouseButton button,
                               @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        super(device, button, callbackSupplier);
    }
    /* @formatter:on */

    @Override
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        switch (event.type) {
            case HOLD:
                state.held = true;
                break;
            case RELEASE:
                state.held = false;
                break;
        }
    }

    @Override
    protected boolean isPressed() {
        return state.isClicked();
    }

}
