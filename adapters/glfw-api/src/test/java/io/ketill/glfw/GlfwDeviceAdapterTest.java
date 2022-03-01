package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlfwDeviceAdapterTest {

    @Test
    void __init__() {
        IoDevice device = mock(IoDevice.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);

        /*
         * For a GLFW device adapter to function, a valid window
         * pointer must be provided. As such, throw an exception
         * if the pointer is NULL or does not point to a valid
         * GLFW window.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwDeviceAdapter(device, registry, 0x00));
        assertThrows(IllegalArgumentException.class,
                () -> new MockGlfwDeviceAdapter(device, registry, 0x01));
    }

}