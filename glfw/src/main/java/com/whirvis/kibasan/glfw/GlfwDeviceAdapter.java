package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.InputDevice;

public abstract class GlfwDeviceAdapter<I extends InputDevice>
        extends DeviceAdapter<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceAdapter(long ptr_glfwWindow) {
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
