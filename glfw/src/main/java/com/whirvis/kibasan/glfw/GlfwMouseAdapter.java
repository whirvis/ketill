package com.whirvis.kibasan.glfw;

import com.whirvis.kibasan.Button1b;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.pc.Cursor2f;
import com.whirvis.kibasan.pc.Mouse;
import com.whirvis.kibasan.pc.MouseButton;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;

import static com.whirvis.kibasan.pc.Mouse.*;
import static org.lwjgl.glfw.GLFW.*;

public class GlfwMouseAdapter extends GlfwDeviceAdapter<Mouse> {

    protected final double[] xPos;
    protected final double[] yPos;
    protected boolean wasVisible;

    public GlfwMouseAdapter(@NotNull Mouse mouse,
                            @NotNull MappedFeatureRegistry registry,
                            long ptr_glfwWindow) {
        super(mouse, registry, ptr_glfwWindow);
        this.xPos = new double[1];
        this.yPos = new double[1];
        this.wasVisible = true; /* visible by default */
    }

    protected void mapButton(@NotNull MouseButton button, int glfwButton) {
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
    protected void updateButton(@NotNull Button1b button, int glfwButton) {
        int status = glfwGetMouseButton(ptr_glfwWindow, glfwButton);
        button.pressed = status >= GLFW_PRESS;
    }

    @FeatureAdapter
    protected void updateCursor(@NotNull Cursor2f cursor) {
        Vector2fc requested = cursor.getRequestedPos();
        if (requested != null) {
            cursor.x = requested.x();
            cursor.y = requested.y();
            glfwSetCursorPos(ptr_glfwWindow, cursor.x, cursor.y);
        } else {
            cursor.x = (float) this.xPos[0];
            cursor.y = (float) this.yPos[0];
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
