package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;

public abstract class GlfwDeviceSeeker<I extends IoDevice>
        extends IoDeviceSeeker<I> {

    protected final long ptr_glfwWindow;

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwDeviceSeeker(long ptr_glfwWindow) {
        this.ptr_glfwWindow = GlfwUtils.requireWindow(ptr_glfwWindow);
    }

}
