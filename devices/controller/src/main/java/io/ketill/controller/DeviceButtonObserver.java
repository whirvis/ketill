package io.ketill.controller;

import io.ketill.IoDevice;
import io.ketill.pressable.PressableFeatureEvent;
import io.ketill.pressable.PressableIoFeatureObserver;
import io.ketill.pressable.PressableIoFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

final class DeviceButtonMonitor
        extends PressableIoFeatureObserver<DeviceButton, ButtonStateZ> {

    @Override
    protected boolean isPressed() {
        return internalState.pressed;
    }

}
