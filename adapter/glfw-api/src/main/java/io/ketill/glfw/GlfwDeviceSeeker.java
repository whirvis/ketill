package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;

/**
 * Scan for I/O devices currently connected to a GLFW window.
 * <p>
 * When a sought after device connects to the window, the appropriate
 * {@link IoDevice} and adapter will be automatically instantiated. However,
 * after creation, they  must be polled manually. All currently discovered
 * devices can be polled using {@link #pollDevices()}.
 * <p>
 * <b>Requirements:</b> The {@code glfwPollEvents()} function <i>must</i>
 * be called before scanning. Failure to do so will result in out-of-date
 * device connection status being returned to the seeker by GLFW.
 * <p>
 * Furthermore, for a GLFW device seeker to work as expected, scans must
 * be performed periodically via {@link #seek()}. It is recommended to
 * perform a scan once every application update.
 * <p>
 * <b>Thread safety:</b> This class is <i>not</i> thread-safe. Operations
 * like scanning must be run on the thread which created the GLFW window.
 *
 * @param <I> the I/O device type.
 * @see #discoverDevice(IoDevice)
 * @see #forgetDevice(IoDevice)
 * @see GlfwDeviceAdapter
 * @see GlfwJoystickSeeker
 */
public abstract class GlfwDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> {

    /**
     * The pointer to the GLFW window that this seeker interfaces with.
     * <p>
     * This field is {@code protected} so it is visible to child classes.
     * This allows them to interface with the GLFW window directly.
     */
    protected final long ptr_glfwWindow;

    /**
     * Constructs a new {@code GlfwDeviceSeeker}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     */
    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = GlfwUtils.requireWindow(ptr_glfwWindow);
    }

}
