package io.ketill.controller;

import io.ketill.AdapterSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MockController extends Controller {

    MockController(@NotNull AdapterSupplier<?> adapterSupplier,
                   @Nullable AnalogStick ls, @Nullable AnalogStick rs,
                   @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt,
                   boolean registerFields, boolean initAdapter) {
        super("mock_controller", adapterSupplier, ls, rs, lt, rt,
                registerFields, initAdapter);
    }

    MockController(@NotNull AdapterSupplier<?> adapterSupplier,
                   @Nullable AnalogStick ls, @Nullable AnalogStick rs,
                   @Nullable AnalogTrigger lt, @Nullable AnalogTrigger rt) {
        super("mock_controller", adapterSupplier, ls, rs, lt, rt);
    }

}
