package io.ketill.glfw;

import io.ketill.controller.Controller;
import org.jetbrains.annotations.NotNull;

class MockGlfwJoystickSeeker extends GlfwJoystickSeeker<Controller> {

    boolean wrangledGuid, releasedGuid;

    public MockGlfwJoystickSeeker(Class<Controller> type, long ptr_glfwWindow) {
        super(type, ptr_glfwWindow);
    }

    public MockGlfwJoystickSeeker(long ptr_glfwWindow) {
        this(Controller.class, ptr_glfwWindow);
    }

    @Override
    protected void guidWrangled(@NotNull String guid,
                                @NotNull GlfwJoystickWrangler<Controller> wrangler) {
        this.wrangledGuid = true;
    }


    @Override
    protected void guidReleased(@NotNull String guid,
                                @NotNull GlfwJoystickWrangler<Controller> wrangler) {
        this.releasedGuid = true;
    }

}
