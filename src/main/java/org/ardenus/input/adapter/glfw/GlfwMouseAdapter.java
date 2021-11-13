package org.ardenus.input.adapter.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.ardenus.input.Mouse;
import org.ardenus.input.adapter.AdapterMapping;
import org.ardenus.input.adapter.FeatureAdapter;
import org.ardenus.input.feature.Button1b;
import org.ardenus.input.feature.Cursor2f;
import org.joml.Vector2fc;

public class GlfwMouseAdapter extends GlfwDeviceAdapter<Mouse> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwButtonMapping
			BUTTON_1 = new GlfwButtonMapping(Mouse.BUTTON_1,
					GLFW_MOUSE_BUTTON_1),
			BUTTON_2 = new GlfwButtonMapping(Mouse.BUTTON_2,
					GLFW_MOUSE_BUTTON_2),
			BUTTON_3 = new GlfwButtonMapping(Mouse.BUTTON_3,
					GLFW_MOUSE_BUTTON_3),
			BUTTON_4 = new GlfwButtonMapping(Mouse.BUTTON_4,
					GLFW_MOUSE_BUTTON_4),
			BUTTON_5 = new GlfwButtonMapping(Mouse.BUTTON_5,
					GLFW_MOUSE_BUTTON_5),
			BUTTON_6 = new GlfwButtonMapping(Mouse.BUTTON_6,
					GLFW_MOUSE_BUTTON_6),
			BUTTON_7 = new GlfwButtonMapping(Mouse.BUTTON_7,
					GLFW_MOUSE_BUTTON_7),
			BUTTON_8 = new GlfwButtonMapping(Mouse.BUTTON_8,
					GLFW_MOUSE_BUTTON_8);

	@AdapterMapping
	public static final GlfwCursorMapping
			CURSOR = new GlfwCursorMapping(Mouse.CURSOR);
	/* @formatter: on */

	private final double[] xPos;
	private final double[] yPos;
	private boolean wasVisible;

	public GlfwMouseAdapter(long ptr_glfwWindow) {
		super(ptr_glfwWindow);
		this.xPos = new double[1];
		this.yPos = new double[1];
		this.wasVisible = true; /* visible by default */
	}

	@Override
	public boolean isConnected() {
		return true; /* mouse is always connected */
	}

	@FeatureAdapter
	public void isPressed(GlfwButtonMapping mapping, Button1b button) {
		int status = glfwGetMouseButton(ptr_glfwWindow, mapping.glfwButton);
		button.pressed = status >= GLFW_PRESS;
	}

	@FeatureAdapter
	public void updateCursor(GlfwCursorMapping mapping, Cursor2f cursor) {
		Vector2fc requested = cursor.getRequestedPos();
		if (requested != null) {
			cursor.x = requested.x();
			cursor.y = requested.y();
			glfwSetCursorPos(ptr_glfwWindow, cursor.x, cursor.y);
		} else {
			cursor.x = (float) this.xPos[0];
			cursor.y = (float) this.yPos[0];
		}

		boolean visible = cursor.visible;
		if (!wasVisible && visible) {
			glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			this.wasVisible = visible;
		} else if (wasVisible && !visible) {
			glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
			this.wasVisible = visible;
		}
	}

	@Override
	public void poll() {
		glfwGetCursorPos(ptr_glfwWindow, xPos, yPos);
	}

}
