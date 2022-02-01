package com.whirvis.ketill.glfw;

import com.whirvis.ketill.DeviceAdapter;
import com.whirvis.ketill.InputDevice;
import com.whirvis.ketill.MappedFeatureRegistry;
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
