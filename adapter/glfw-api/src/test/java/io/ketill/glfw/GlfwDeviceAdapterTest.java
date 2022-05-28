package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.MappedFeatureRegistry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlfwDeviceAdapterTest {

    @Test
    void testInit() {
        IoDevice device = mock(IoDevice.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);

        /*
         * For a GLFW device adapter to function, a valid window pointer must
         * be provided by the user. The GlfwDeviceAdapter class should make a
         * call to GlfwUtils.requireWindow(). Since a NULL pointer was passed
         * here, a NullPointerException should be thrown.
         */
        assertThrows(NullPointerException.class,
                () -> new MockGlfwDeviceAdapter(device, registry, 0x00));
    }

}