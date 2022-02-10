package io.ketill.glfw;

public class GlfwStickMapping {

    public final int glfwAxisX;
    public final int glfwAxisY;
    public final int glfwZButton;
    public final boolean hasZButton;

    private GlfwStickMapping(int glfwAxisX, int glfwAxisY, int glfwZButton,
                             boolean hasZButton) {
        this.glfwAxisX = glfwAxisX;
        this.glfwAxisY = glfwAxisY;
        this.glfwZButton = glfwZButton;
        this.hasZButton = hasZButton;
    }

    public GlfwStickMapping(int glfwAxisX, int glfwAxisY, int glfwZButton) {
        this(glfwAxisX, glfwAxisY, glfwZButton, true);
    }

    public GlfwStickMapping(int glfwAxisX, int glfwAxisY) {
        this(glfwAxisX, glfwAxisY, -1, false);
    }

}
