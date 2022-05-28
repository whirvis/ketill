package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

class MockGlfwDeviceAdapter extends GlfwDeviceAdapter<IoDevice> {

    MockGlfwDeviceAdapter(@NotNull IoDevice device,
                          @NotNull MappedFeatureRegistry registry,
                          long ptr_glfwWindow) {
        super(device, registry, ptr_glfwWindow);
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
