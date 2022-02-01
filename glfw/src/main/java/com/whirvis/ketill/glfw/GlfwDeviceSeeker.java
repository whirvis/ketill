package com.whirvis.ketill.glfw;

import com.whirvis.ketill.DeviceSeeker;
import com.whirvis.ketill.InputDevice;

public abstract class GlfwDeviceSeeker<I extends InputDevice>
        extends DeviceSeeker<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
