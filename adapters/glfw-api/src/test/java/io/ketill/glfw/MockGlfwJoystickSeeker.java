package io.ketill.glfw;

import io.ketill.IoDevice;
import org.jetbrains.annotations.NotNull;

class MockGlfwJoystickSeeker extends GlfwJoystickSeeker<IoDevice> {

    boolean wrangledGuid, releasedGuid;

    public MockGlfwJoystickSeeker(Class<IoDevice> type, long ptr_glfwWindow) {
        super(type, ptr_glfwWindow);
    }

    public MockGlfwJoystickSeeker(long ptr_glfwWindow) {
        this(IoDevice.class, ptr_glfwWindow);
    }

    @Override
    protected void guidWrangled(@NotNull String guid,
                                @NotNull GlfwJoystickWrangler<IoDevice> wrangler) {
        this.wrangledGuid = true;
    }


    @Override
    protected void guidReleased(@NotNull String guid,
                                @NotNull GlfwJoystickWrangler<IoDevice> wrangler) {
        this.releasedGuid = true;
    }

}
