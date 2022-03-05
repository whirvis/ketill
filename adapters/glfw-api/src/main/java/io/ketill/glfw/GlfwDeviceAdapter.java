package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * GLFW adapters map data from a GLFW window to an {@link IoDevice}. These
 * allow for seamless integration with LWJGL's bindings to the GLFW API.
 *
 * @param <I> the I/O device type.
 * @see AdapterSupplier
 * @see GlfwDeviceSeeker
 * @see GlfwJoystickAdapter
 */
public abstract class GlfwDeviceAdapter<I extends IoDevice>
        extends IoDeviceAdapter<I> {

    protected final long ptr_glfwWindow;

    /**
     * @param device         the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code device} or {@code registry}
     *                                  are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwDeviceAdapter(@NotNull I device,
                             @NotNull MappedFeatureRegistry registry,
                             long ptr_glfwWindow) {
        super(device, registry);
        this.ptr_glfwWindow = GlfwUtils.requireWindow(ptr_glfwWindow);
    }

}
