package io.ketill.glfw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlfwDeviceSeekerTest {

    @Test
    void __init__() {
        /*
         * For a GLFW device seeker to function, a valid window pointer must
         * be provided. As such, throw an exception if the pointer is NULL or
         * does not point to a valid GLFW window.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwDeviceSeeker(0x00));
        assertThrows(IllegalArgumentException.class,
                () -> new MockGlfwDeviceSeeker(0x01));
    }

}