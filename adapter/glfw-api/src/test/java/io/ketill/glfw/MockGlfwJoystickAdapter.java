package io.ketill.glfw;

import io.ketill.MappedFeatureRegistry;
import io.ketill.controller.Controller;
import org.jetbrains.annotations.NotNull;

class MockGlfwJoystickAdapter extends GlfwJoystickAdapter<Controller> {

    public MockGlfwJoystickAdapter(@NotNull Controller controller,
                                   @NotNull MappedFeatureRegistry registry,
                                   long ptr_glfwWindow, int glfwJoystick) {
        super(controller, registry, ptr_glfwWindow, glfwJoystick);
    }

    @Override
    protected void initAdapter() {
        /* nothing to initialize */
    }

}
