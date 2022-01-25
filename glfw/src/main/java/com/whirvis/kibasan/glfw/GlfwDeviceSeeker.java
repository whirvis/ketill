package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.DeviceSeeker;
import com.whirvis.kibasan.InputDevice;

public abstract class GlfwDeviceSeeker<I extends InputDevice>
        extends DeviceSeeker<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
