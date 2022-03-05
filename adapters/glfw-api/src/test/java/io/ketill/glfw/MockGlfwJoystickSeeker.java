package io.ketill.glfw;

import io.ketill.IoDevice;

class MockGlfwJoystickSeeker extends GlfwJoystickSeeker<IoDevice> {

    @RelativeGuidPath("/json_device_guids/")
    static class WithRelativePath extends MockGlfwJoystickSeeker {

        public WithRelativePath(long ptr_glfwWindow) {
            super(ptr_glfwWindow);
        }

    }

    @RelativeGuidPath("invalid_guid_resource_path/")
    static class WithInvalidPath0 extends MockGlfwJoystickSeeker {

        public WithInvalidPath0(long ptr_glfwWindow) {
            super(ptr_glfwWindow);
        }

    }

    @RelativeGuidPath("/invalid_guid_resource_path")
    static class WithInvalidPath1 extends MockGlfwJoystickSeeker {

        public WithInvalidPath1(long ptr_glfwWindow) {
            super(ptr_glfwWindow);
        }

    }

    public MockGlfwJoystickSeeker(Class<IoDevice> type, long ptr_glfwWindow) {
        super(type, ptr_glfwWindow);
    }

    public MockGlfwJoystickSeeker(long ptr_glfwWindow) {
        this(IoDevice.class, ptr_glfwWindow);
    }

}
