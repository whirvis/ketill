package com.whirvis.ketill.glfw;

import com.whirvis.ketill.IoDeviceSeeker;
import com.whirvis.ketill.IoDevice;

public abstract class GlfwDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
