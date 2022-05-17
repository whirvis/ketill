package io.ketill.pressable;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MockIoDevice extends IoDevice implements PressableIoFeatureSupport {

    private PressableIoFeatureConfigView config;

    MockIoDevice(@NotNull String id,
                 @NotNull AdapterSupplier<MockIoDevice> adapterSupplier) {
        super(id, adapterSupplier);
        this.config = PressableIoFeatureConfig.DEFAULT;
    }

    MockIoDevice() {
        this("mock", MockIoDeviceAdapter::new);
    }

    @Override
    public void usePressableConfig(@Nullable PressableIoFeatureConfigView config) {
        this.config = config;
    }

    @Override
    public @NotNull PressableIoFeatureConfigView getPressableConfig() {
        return this.config;
    }
}
