package io.ketill.glfw;

import io.ketill.IoDevice;

class MockGlfwDeviceSeeker extends GlfwDeviceSeeker<IoDevice> {

    MockGlfwDeviceSeeker(long ptr_glfwWindow) {
        super(ptr_glfwWindow);
    }

    @Override
    protected void seekImpl() {
        /* nothing to seek */
    }

}
