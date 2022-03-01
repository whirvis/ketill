package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;

class MockGlfwDeviceAdapter extends GlfwDeviceAdapter<IoDevice> {

    MockGlfwDeviceAdapter(IoDevice device, MappedFeatureRegistry registry,
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
