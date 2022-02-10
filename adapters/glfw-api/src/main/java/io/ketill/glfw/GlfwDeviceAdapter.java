package io.ketill.glfw;

import io.ketill.IoDeviceAdapter;
import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class GlfwDeviceAdapter<I extends IoDevice>
        extends IoDeviceAdapter<I> {

    protected final long ptr_glfwWindow;

    public GlfwDeviceAdapter(@NotNull I device,
                             @NotNull MappedFeatureRegistry registry,
                             long ptr_glfwWindow) {
        super(device, registry);
        this.ptr_glfwWindow = ptr_glfwWindow;
    }

}
