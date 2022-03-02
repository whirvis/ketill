package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

class MockGlfwJoystickAdapter extends GlfwJoystickAdapter<IoDevice> {

    public MockGlfwJoystickAdapter(@NotNull IoDevice device,
                                   @NotNull MappedFeatureRegistry registry,
                                   long ptr_glfwWindow, int glfwJoystick) {
        super(device, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        /* nothing to initialize */
    }

}
