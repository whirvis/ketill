package io.ketill.xbox;

import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

class MockXboxAdapter extends IoDeviceAdapter<XboxController> {

    MockXboxAdapter(@NotNull XboxController device,
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
