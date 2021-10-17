package org.ardenus.engine.input.device.adapter.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.ardenus.engine.input.device.Mouse;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Button1b;
import org.joml.Vector2f;

public class GLFWMouseAdapter extends GLFWDeviceAdapter<Mouse> {

	/* @formatter: off */
	@AdapterMapping
	public static final ButtonMapping
			BUTTON_1 = new GLFWButtonMapping(Mouse.BUTTON_1,
					GLFW_MOUSE_BUTTON_1),
			BUTTON_2 = new GLFWButtonMapping(Mouse.BUTTON_2,
					GLFW_MOUSE_BUTTON_2),
			BUTTON_3 = new GLFWButtonMapping(Mouse.BUTTON_3,
					GLFW_MOUSE_BUTTON_3),
			BUTTON_4 = new GLFWButtonMapping(Mouse.BUTTON_4,
					GLFW_MOUSE_BUTTON_4),
			BUTTON_5 = new GLFWButtonMapping(Mouse.BUTTON_5,
					GLFW_MOUSE_BUTTON_5),
			BUTTON_6 = new GLFWButtonMapping(Mouse.BUTTON_6,
					GLFW_MOUSE_BUTTON_6),
			BUTTON_7 = new GLFWButtonMapping(Mouse.BUTTON_7,
					GLFW_MOUSE_BUTTON_7),
			BUTTON_8 = new GLFWButtonMapping(Mouse.BUTTON_8,
					GLFW_MOUSE_BUTTON_8);
	
	@AdapterMapping
	public static final AnalogMapping<?>
			CURSOR = new GLFWCursorMapping(Mouse.CURSOR);
	/* @formatter: on */
	
	private final double[] xPos;
	private final double[] yPos;

	public GLFWMouseAdapter(long ptr_glfwWindow) {
		super(ptr_glfwWindow);
		this.xPos = new double[1];
		this.yPos = new double[1];
	}

	@Override
	public boolean isConnected() {
		return true; /* mouse is always connected */
	}

	@FeatureAdapter
	public void isPressed(GLFWButtonMapping mapping, Button1b button) {
		int status = glfwGetMouseButton(ptr_glfwWindow, mapping.glfwButton);
		button.pressed = status >= GLFW_PRESS;
	}
	
	@FeatureAdapter
	public void getCursor(GLFWCursorMapping mapping, Vector2f pos) {
		pos.x = (float) this.xPos[0];
		pos.y = (float) this.yPos[0];
	}

	@Override
	public void poll() {
		glfwGetCursorPos(ptr_glfwWindow, xPos, yPos);
	}

}
