package io.ketill.pressable;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

class MockIoDevice extends IoDevice implements PressableFeatureSupport {

    private PressableFeatureConfigView config;

    MockIoDevice(@NotNull String id,
                 @NotNull AdapterSupplier<MockIoDevice> adapterSupplier) {
        super(id, adapterSupplier);
        this.config = PressableFeatureConfig.DEFAULT;
    }

    MockIoDevice() {
        this("mock", MockIoDeviceAdapter::new);
    }

    @Override
    public void usePressableConfig(@Nullable PressableFeatureConfigView config) {
        this.config = config;
    }

    @Override
    public @NotNull PressableFeatureConfigView getPressableConfig() {
        return this.config;
    }

    @Override
    public void onPressableEvent(@Nullable Consumer<PressableFeatureEvent> callback) {
        throw new UnsupportedOperationException();
    }
}
