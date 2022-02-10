package io.ketill.glfw;

import io.ketill.IoDeviceSeeker;
import io.ketill.IoDevice;

public abstract class GlfwDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
