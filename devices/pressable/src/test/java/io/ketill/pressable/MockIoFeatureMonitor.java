package io.ketill.pressable;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

class MockIoFeatureMonitor
        extends PressableFeatureMonitor<MockIoFeature, Object> {

    boolean pressed;
    PressableFeatureEvent lastEvent;

    /* @formatter:off */
    <I extends IoDevice & PressableFeatureSupport>
    MockIoFeatureMonitor(@NotNull I device,
                         @NotNull MockIoFeature feature,
                         @NotNull Object internalState,
                         @NotNull Supplier<@Nullable Consumer<PressableFeatureEvent>> callbackSupplier) {
        super(device, feature, internalState, callbackSupplier);
    }
    /* @formatter:on */

    @Override
    protected boolean isPressed() {
        return this.pressed;
    }

    @Override
    protected void eventFired(@NotNull PressableFeatureEvent event) {
        this.lastEvent = event;
    }

}
