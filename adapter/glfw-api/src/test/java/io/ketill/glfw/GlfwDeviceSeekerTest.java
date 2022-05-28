package io.ketill.glfw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlfwDeviceSeekerTest {

    @Test
    void testInit() {
        /*
         * For a GLFW device adapter to function, a valid window pointer must
         * be provided by the user. The GlfwDeviceSeeker class should make a
         * call to GlfwUtils.requireWindow(). Since a NULL pointer was passed
         * here, a NullPointerException should be thrown.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwDeviceSeeker(0x00));
    }

}