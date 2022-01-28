package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.DeviceAdapter;
import com.whirvis.kibasan.InputDevice;
import com.whirvis.kibasan.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class GlfwDeviceAdapter<I extends InputDevice>
        extends DeviceAdapter<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceAdapter(@NotNull I device,
                             @NotNull MappedFeatureRegistry registry,
                             long ptr_glfwWindow) {
        super(device, registry);
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
