package io.ketill.pressable;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import io.ketill.IoDeviceObserver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class MockIoDevice extends IoDevice {

    static class WithSupport extends MockIoDevice
            implements PressableIoFeatureSupport {

        boolean requestedConfig;
        private PressableIoFeatureConfigView config;

        @Override
        public void usePressableConfig(@Nullable PressableIoFeatureConfigView config) {
            this.config = config;
        }

        @Override
        public @NotNull PressableIoFeatureConfigView getPressableConfig() {
            this.requestedConfig = true;
            return this.config;
        }

    }

    final IoDeviceObserver observerAccess;

    MockIoDevice(@NotNull String id,
                 @NotNull AdapterSupplier<MockIoDevice> adapterSupplier) {
        super(id, adapterSupplier);
        this.observerAccess = this.observer;
    }

    MockIoDevice() {
        this("mock", MockIoDeviceAdapter::new);
    }

}
