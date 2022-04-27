package io.ketill.glfw.pc;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.glfw.GlfwDeviceAdapter;
import io.ketill.glfw.WranglerMethod;
import io.ketill.pc.MouseClickZ;
import io.ketill.pc.CursorStateZ;
import io.ketill.pc.Mouse;
import io.ketill.pc.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

import java.util.Objects;

import static io.ketill.pc.Mouse.*;
import static org.lwjgl.glfw.GLFW.*;

public class GlfwMouseAdapter extends GlfwDeviceAdapter<Mouse> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @return the wrangled mouse.
     * @throws NullPointerException     if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    @WranglerMethod
    public static @NotNull Mouse wrangle(long ptr_glfwWindow) {
        return new Mouse((d, r) -> new GlfwMouseAdapter(d, r,
                ptr_glfwWindow));
    }

    protected final double[] xPos;
    protected final double[] yPos;
    protected boolean wasVisible;

    /**
     * @param mouse          the device which owns this adapter.
     * @param registry       the device's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code mouse} or
     *                                  {@code registry} are {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     */
    public GlfwMouseAdapter(@NotNull Mouse mouse,
                            @NotNull MappedFeatureRegistry registry,
                            long ptr_glfwWindow) {
        super(mouse, registry, ptr_glfwWindow);
        this.xPos = new double[1];
        this.yPos = new double[1];
        this.wasVisible = true; /* visible by default */
    }


    /**
     * @param button     the mouse button to map.
     * @param glfwButton the GLFW button to map {@code button} to.
     * @throws NullPointerException     if {@code button} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwButton} is negative.
     * @see #updateButton(MouseClickZ, int)
     */
    @MappingMethod
    protected void mapButton(@NotNull MouseButton button, int glfwButton) {
        Objects.requireNonNull(button, "button");
        if (glfwButton < 0) {
            throw new IllegalArgumentException("glfwButton < 0");
        }
        registry.mapFeature(button, glfwButton, this::updateButton);
    }

    @Override
    public void initAdapter() {
        this.mapButton(BUTTON_M1, GLFW_MOUSE_BUTTON_1);
        this.mapButton(BUTTON_M2, GLFW_MOUSE_BUTTON_2);
        this.mapButton(BUTTON_M3, GLFW_MOUSE_BUTTON_3);
        this.mapButton(BUTTON_M4, GLFW_MOUSE_BUTTON_4);
        this.mapButton(BUTTON_M5, GLFW_MOUSE_BUTTON_5);
        this.mapButton(BUTTON_M6, GLFW_MOUSE_BUTTON_6);
        this.mapButton(BUTTON_M7, GLFW_MOUSE_BUTTON_7);
        this.mapButton(BUTTON_M8, GLFW_MOUSE_BUTTON_8);
        registry.mapFeature(FEATURE_CURSOR, this::updateCursor);
    }

    @FeatureAdapter
    protected void updateButton(@NotNull MouseClickZ click, int glfwButton) {
        int status = glfwGetMouseButton(ptr_glfwWindow, glfwButton);
        click.pressed = status >= GLFW_PRESS;
    }

    @FeatureAdapter
    protected void updateCursor(@NotNull CursorStateZ cursor) {
        Vector2fc requested = cursor.requestedPos;
        cursor.requestedPos = null;
        if (requested != null) {
            cursor.currentPos.set(requested);
            glfwSetCursorPos(ptr_glfwWindow, requested.x(), requested.y());
        } else {
            cursor.currentPos.x = (float) this.xPos[0];
            cursor.currentPos.y = (float) this.yPos[0];
        }

        if (!wasVisible && cursor.visible) {
            glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            this.wasVisible = true;
        } else if (wasVisible && !cursor.visible) {
            glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            this.wasVisible = false;
        }
    }

    @Override
    protected void pollDevice() {
        glfwGetCursorPos(ptr_glfwWindow, xPos, yPos);
    }

    @Override
    protected boolean isDeviceConnected() {
        return true; /* mouse is always connected */
    }

}
