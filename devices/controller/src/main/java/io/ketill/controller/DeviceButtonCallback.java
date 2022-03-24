package io.ketill.controller;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface DeviceButtonCallback<C extends Controller> {

    void execute(@NotNull C controller, @NotNull DeviceButton button,
                 @Nullable DeviceButtonEvent event, boolean held);

}
