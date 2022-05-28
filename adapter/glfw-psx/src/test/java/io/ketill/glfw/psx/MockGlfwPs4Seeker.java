package io.ketill.glfw.psx;

import org.mockito.MockedStatic;

import static org.lwjgl.glfw.GLFW.*;

class MockGlfwPs4Seeker extends GlfwPs4Seeker {

    private static final String GUID = "MOCK_PS4";

    MockGlfwPs4Seeker(long ptr_glfwWindow) {
        super(ptr_glfwWindow);
        this.wrangleGuid(GUID, GlfwPs4Adapter::wrangle);
    }

    void connectJoystick(MockedStatic<org.lwjgl.glfw.GLFW> glfw, int joystick) {
        glfw.when(() -> glfwGetJoystickGUID(joystick)).thenReturn(GUID);
        glfw.when(() -> glfwJoystickPresent(joystick)).thenReturn(true);
        this.seek(); /* trigger connection */
    }

    @SuppressWarnings("SameParameterValue")
    void disconnectJoystick(MockedStatic<org.lwjgl.glfw.GLFW> glfw,
                            int joystick) {
        glfw.when(() -> glfwGetJoystickGUID(joystick)).thenReturn(null);
        glfw.when(() -> glfwJoystickPresent(joystick)).thenReturn(false);
        this.seek(); /* trigger disconnection */
    }

}
