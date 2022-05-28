package io.ketill.glfw;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Utility methods for working with GLFW.
 */
public final class GlfwUtils {

    private static final long NULL_PTR = 0L;
    private static final int[] WINDOW_SIZE = new int[1];

    private GlfwUtils() {
        /* prevent instantiation */
    }

    /**
     * Checks that the specified GLFW window pointer is valid.
     * <p>
     * <b>Note:</b> If an invalid GLFW window pointer is passed, this method
     * will cause an {@code EXCEPTION_ACCESS_VIOLATION} to be raised. This
     * will cause the JVM to crash. <i>This is by design.</i> If the program
     * crashes here, it will be clear what caused the exception to be raised.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @return {@code ptr_glfwWindow} if valid.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has value of zero).
     */
    public static long requireWindow(long ptr_glfwWindow) {
        if (ptr_glfwWindow == NULL_PTR) {
            String msg = "GLFW window pointer cannot be NULL";
            throw new NullPointerException(msg);
        }

        /*
         * Attempt to grab the window size. If an invalid pointer was passed,
         * this will likely cause the program to crash. This is good, as it
         * ensures the program will crash sooner than later (in turn, making
         * finding the bug easier.)
         */
        glfwGetWindowSize(ptr_glfwWindow, WINDOW_SIZE, WINDOW_SIZE);

        return ptr_glfwWindow;
    }

    /**
     * Checks that the specified GLFW joystick is valid.
     *
     * @param glfwJoystick the GLFW joystick to validate.
     * @return {@code glfwJoystick} if valid.
     * @throws IllegalArgumentException if {@code glfwJoystick} is
     *                                  negative or greater than
     *                                  {@code GLFW_JOYSTICK_LAST}.
     */
    public static int requireJoystick(int glfwJoystick) {
        if (glfwJoystick < 0 || glfwJoystick > GLFW_JOYSTICK_LAST) {
            throw new IllegalArgumentException("no such GLFW joystick");
        }
        return glfwJoystick;
    }

    /**
     * Checks that the specified GLFW button is valid.
     *
     * @param glfwButton  the GLFW button to validate.
     * @param buttonCount the amount of available buttons.
     * @param paramName   the parameter being validated.
     * @return {@code glfwButton} if valid.
     * @throws NullPointerException     if {@code paramName} is {@code null},
     *                                  empty, or surrounded by whitespace.
     * @throws IllegalArgumentException if {@code buttonCount} is negative;
     *                                  if {@code glfwButton} is negative or
     *                                  not lower than {@code buttonCount}.
     */
    public static int requireButton(int glfwButton, int buttonCount,
                                    @NotNull String paramName) {
        Objects.requireNonNull(paramName, "paramName cannot be null");

        if (paramName.isEmpty()) {
            String msg = "paramName cannot be empty";
            throw new IllegalArgumentException(msg);
        } else if (!paramName.trim().equals(paramName)) {
            String msg = "paramName cannot be surrounded by whitespace";
            throw new IllegalArgumentException(msg);
        } else if (buttonCount < 0) {
            String msg = "buttonCount cannot be negative";
            throw new IllegalArgumentException(msg);
        }

        if (glfwButton < 0) {
            String msg = paramName + " cannot be negative";
            throw new IllegalArgumentException(msg);
        } else if (glfwButton >= buttonCount) {
            String msg = paramName + " must be lower than " + buttonCount;
            throw new IllegalArgumentException(msg);
        }

        return glfwButton;
    }

    /**
     * Checks that the specified GLFW button is valid.
     * <p>
     * <b>Shorthand for:</b> {@link #requireButton(int, int, String)}, with
     * the argument for {@code buttonCount} being {@link Integer#MAX_VALUE}.
     *
     * @param glfwButton the GLFW button to validate.
     * @param paramName  the parameter being validated.
     * @return {@code glfwButton} if valid.
     * @throws NullPointerException     if {@code paramName} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwButton} is negative.
     */
    public static int requireButton(int glfwButton, @NotNull String paramName) {
        return requireButton(glfwButton, Integer.MAX_VALUE, paramName);
    }

    /**
     * Checks that the specified GLFW button is valid.
     * <p>
     * <b>Shorthand for:</b> {@link #requireButton(int, int, String)}, with
     * the argument for {@code paramName} being {@code "GLFW button"}.
     *
     * @param glfwButton  the GLFW button to validate.
     * @param buttonCount the amount of available buttons.
     * @return {@code glfwButton} if valid.
     * @throws IllegalArgumentException if {@code buttonCount} is negative;
     *                                  if {@code glfwButton} is negative or
     *                                  not lower than {@code buttonCount}.
     */
    public static int requireButton(int glfwButton, int buttonCount) {
        return requireButton(glfwButton, buttonCount, "GLFW button");
    }

    /**
     * Checks that the specified GLFW button is valid.
     * <p>
     * <b>Shorthand for:</b> {@link #requireButton(int, int, String)}, with
     * the arguments for {@code buttonCount} being {@link Integer#MAX_VALUE}
     * and {@code paramName} being {@code "GLFW button"}.
     *
     * @param glfwButton the GLFW button to validate.
     * @return {@code glfwButton} if valid.
     * @throws IllegalArgumentException if {@code glfwButton} is negative.
     */
    public static int requireButton(int glfwButton) {
        return requireButton(glfwButton, Integer.MAX_VALUE);
    }

    /**
     * Checks that the specified GLFW axis is valid.
     *
     * @param glfwAxis  the GLFW axis to validate.
     * @param axisCount the amount of available axes.
     * @param paramName the parameter being validated.
     * @return {@code glfwAxis} if valid.
     * @throws NullPointerException     if {@code paramName} is {@code null},
     *                                  empty, or surrounded by whitespace.
     * @throws IllegalArgumentException if {@code axisCount} is negative;
     *                                  if {@code glfwAxis} is negative or
     *                                  not lower than {@code axisCount}.
     */
    public static int requireAxis(int glfwAxis, int axisCount,
                                  @NotNull String paramName) {
        Objects.requireNonNull(paramName, "paramName cannot be null");

        if (paramName.isEmpty()) {
            String msg = "paramName cannot be empty";
            throw new IllegalArgumentException(msg);
        } else if (!paramName.trim().equals(paramName)) {
            String msg = "paramName cannot be surrounded by whitespace";
            throw new IllegalArgumentException(msg);
        } else if (axisCount < 0) {
            String msg = "axisCount cannot be negative";
            throw new IllegalArgumentException(msg);
        }

        if (glfwAxis < 0) {
            String msg = paramName + " cannot be negative";
            throw new IllegalArgumentException(msg);
        } else if (glfwAxis >= axisCount) {
            String msg = paramName + " must be lower than " + axisCount;
            throw new IllegalArgumentException(msg);
        }

        return glfwAxis;
    }

    /**
     * Checks that the specified GLFW axis is valid.
     * <p>
     * <b>Shorthand for:</b> {@link #requireAxis(int, int, String)}, with
     * the argument for {@code axisCount} being {@link Integer#MAX_VALUE}.
     *
     * @param glfwAxis  the GLFW axis to validate.
     * @param paramName the parameter being validated.
     * @return {@code glfwAxis} if valid.
     * @throws NullPointerException     if {@code paramName} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwAxis} is negative.
     */
    public static int requireAxis(int glfwAxis, @NotNull String paramName) {
        return requireAxis(glfwAxis, Integer.MAX_VALUE, paramName);
    }

    /**
     * Checks that the specified GLFW axis is valid.
     * <p>
     * <b>Shorthand for:</b> {@link #requireAxis(int, int, String)}, with
     * the argument for {@code paramName} being {@code "GLFW axis"}.
     *
     * @param glfwAxis  the GLFW axis  to validate.
     * @param axisCount the amount of available axes.
     * @return {@code glfwAxis} if valid.
     * @throws IllegalArgumentException if {@code axisCount} is negative;
     *                                  if {@code glfwAxis} is negative or
     *                                  not lower than {@code axisCount}.
     */
    public static int requireAxis(int glfwAxis, int axisCount) {
        return requireAxis(glfwAxis, axisCount, "GLFW axis");
    }

    /**
     * Checks that the specified GLFW axis is valid.
     * <p>
     * <b>Shorthand for:</b> {@link #requireAxis(int, int, String)}, with
     * the arguments for {@code axisCount} being {@link Integer#MAX_VALUE}
     * and {@code paramName} being {@code "GLFW axis"}.
     *
     * @param glfwAxis the GLFW axis to validate.
     * @return {@code glfwAxis} if valid.
     * @throws IllegalArgumentException if {@code glfwAxis} is negative.
     */
    public static int requireAxis(int glfwAxis) {
        return requireAxis(glfwAxis, Integer.MAX_VALUE);
    }

}
