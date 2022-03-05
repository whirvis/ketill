package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.mockito.MockedStatic;

import static org.lwjgl.glfw.GLFW.*;
import static org.mockito.ArgumentMatchers.*;

class MockGlfw {

    static void mockGlfwWindow(@NotNull MockedStatic<GLFW> glfw,
                               long ptr_glfwWindow) {
        /*
         * Mocking this specific GLFW method is required for
         * a mock GLFW joystick seeker to be created. It is
         * used to validate a pointer to a GLFW window.
         */
        glfw.when(() -> glfwGetWindowSize(eq(ptr_glfwWindow),
                (int[]) any(), any())).thenAnswer(a -> {
            a.getArgument(1, int[].class)[0] = 1024;
            a.getArgument(2, int[].class)[0] = 768;
            return null;
        });
    }

}
