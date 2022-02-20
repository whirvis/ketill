package io.ketill.glfw.nx;

final class JoyConStickMapping {

    final int glfwUp, glfwDown;
    final int glfwLeft, glfwRight;
    final int glfwThumb;

    JoyConStickMapping(int glfwUp, int glfwDown, int glfwLeft, int glfwRight,
                       int glfwThumb) {
        this.glfwUp = glfwUp;
        this.glfwDown = glfwDown;
        this.glfwLeft = glfwLeft;
        this.glfwRight = glfwRight;
        this.glfwThumb = glfwThumb;
    }

}
