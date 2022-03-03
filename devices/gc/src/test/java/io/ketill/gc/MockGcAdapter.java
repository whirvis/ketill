package io.ketill.gc;

import io.ketill.IoDevice;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

class MockGcAdapter<I extends IoDevice> extends IoDeviceAdapter<I> {

    MockGcAdapter(@NotNull I device,
                  @NotNull MappedFeatureRegistry registry) {
        super(device, registry);
    }

    @Override
    protected void initAdapter() {
        /* nothing to initialize */
    }

    @Override
    protected void pollDevice() {
        /* nothing to poll */
    }

    @Override
    protected boolean isDeviceConnected() {
        return false;
    }

}
