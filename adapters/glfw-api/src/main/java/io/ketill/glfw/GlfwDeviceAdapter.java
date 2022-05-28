package io.ketill.glfw;

import io.ketill.AdapterSupplier;
import io.ketill.IoDevice;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * GLFW adapters map data from a GLFW window to an {@link IoDevice}. These
 * allow for seamless integration with LWJGL's bindings to the GLFW API.
 * <p>
 * <b>Note:</b> The {@code glfwPollEvents()} <i>must</i> be called before
 * the adapter is polled. Failure to do so will result in GLFW returning
 * out-of-date input data to the adapter.
 *
 * @param <I> the I/O device type.
 * @see AdapterSupplier
 * @see GlfwDeviceSeeker
 * @see GlfwJoystickAdapter
 */
public abstract class GlfwDeviceAdapter<I extends IoDevice>
        extends IoDeviceAdapter<I> {

    /**
     * The pointer to the GLFW window that this adapter interfaces with.
     * <p>
     * This field is {@code protected} so it is visible to child classes.
     * This allows them to interface with the GLFW window directly.
     */
    protected final long ptr_glfwWindow;

    /**
     * Constructs a new {@code GlfwDeviceAdapter}.
     *
     * @param device         the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code device} or {@code registry}
     *                                  are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     */
    public GlfwDeviceAdapter(@NotNull I device,
                             @NotNull MappedFeatureRegistry registry,
                             long ptr_glfwWindow) {
        super(device, registry);
        this.ptr_glfwWindow = GlfwUtils.requireWindow(ptr_glfwWindow);
    }

}
