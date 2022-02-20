package io.ketill.glfw;

import io.ketill.MappingType;

@MappingType
public class GlfwStickMapping {

    public final int glfwXAxis;
    public final int glfwYAxis;
    public final int glfwZButton;
    public final boolean hasZButton;

    private GlfwStickMapping(int glfwXAxis, int glfwYAxis, int glfwZButton,
                             boolean hasZButton) {
        if (glfwXAxis < 0) {
            throw new IllegalArgumentException("glfwXAxis < 0");
        } else if (glfwYAxis < 0) {
            throw new IllegalArgumentException("glfwYAxis < 0");
        } else if (glfwZButton < 0 && hasZButton) {
            throw new IllegalArgumentException("glfwZButton < 0");
        }

        this.glfwXAxis = glfwXAxis;
        this.glfwYAxis = glfwYAxis;
        this.glfwZButton = glfwZButton;
        this.hasZButton = hasZButton;
    }

    /**
     * @param glfwXAxis   the GLFW axis for the X-axis.
     * @param glfwYAxis   the GLFW axis for the Y-axis.
     * @param glfwZButton the GLFW button for the thumb button.
     * @throws IllegalArgumentException if {@code glfwXAxis},
     *                                  {@code glfwYAxis}, or
     *                                  {@code glfwZButton} are negative.
     */
    public GlfwStickMapping(int glfwXAxis, int glfwYAxis, int glfwZButton) {
        this(glfwXAxis, glfwYAxis, glfwZButton, true);
    }

    /**
     * @param glfwAxisX the GLFW axis for the X-axis.
     * @param glfwAxisY the GLFW axis for the Y-axis.
     * @throws IllegalArgumentException if {@code glfwXAxis} or
     *                                  {@code glfwYAxis} are negative.
     */
    public GlfwStickMapping(int glfwAxisX, int glfwAxisY) {
        this(glfwAxisX, glfwAxisY, -1, false);
    }

}
