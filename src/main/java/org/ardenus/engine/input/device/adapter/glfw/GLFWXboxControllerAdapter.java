package org.ardenus.engine.input.device.adapter.glfw;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.analog.Trigger1f;
import org.ardenus.engine.input.device.controller.XboxController;
import org.joml.Vector3f;

/**
 * A GLFW joystick adapter for an {@link XboxController}.
 * 
 * @see GLFWJoystickAdapter
 */
public class GLFWXboxControllerAdapter extends
		GLFWJoystickAdapter<XboxController, GLFWMappedAnalog<?>, GLFWMappedButton> {

	@AnalogMapping
	private static final GLFWMappedAnalog<?> MA_LS =
			new GLFWMappedAnalogStick(XboxController.STICK_L, 0, 1),
			MA_RS = new GLFWMappedAnalogStick(XboxController.STICK_R, 2, 3),
			MA_LT = new GLFWMappedAnalogTrigger(XboxController.TRIGGER_L, 4),
			MA_RT = new GLFWMappedAnalogTrigger(XboxController.TRIGGER_R, 5);

	@ButtonMapping
	private static final GLFWMappedButton MB_A =
			new GLFWMappedButton(XboxController.BUTTON_A, 0),
			MB_B = new GLFWMappedButton(XboxController.BUTTON_B, 1),
			MB_X = new GLFWMappedButton(XboxController.BUTTON_X, 2),
			MB_Y = new GLFWMappedButton(XboxController.BUTTON_Y, 3),
			MB_LB = new GLFWMappedButton(XboxController.BUTTON_LB, 4),
			MB_RB = new GLFWMappedButton(XboxController.BUTTON_RB, 5),
			MB_MENU = new GLFWMappedButton(XboxController.BUTTON_MENU, 6),
			MB_PAUSE = new GLFWMappedButton(XboxController.BUTTON_PAUSE, 7),
			MB_LS = new GLFWMappedButton(XboxController.BUTTON_LS, 8),
			MB_RS = new GLFWMappedButton(XboxController.BUTTON_RS, 9),
			MB_UP = new GLFWMappedButton(XboxController.BUTTON_UP, 10),
			MB_RIGHT = new GLFWMappedButton(XboxController.BUTTON_RIGHT, 11),
			MB_DOWN = new GLFWMappedButton(XboxController.BUTTON_DOWN, 12),
			MB_LEFT = new GLFWMappedButton(XboxController.BUTTON_LEFT, 13);

	/**
	 * Constructs a new {@code GLFWXboxControllerAdapter}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 * @throws InputException
	 *             if an input error occurs.
	 */
	public GLFWXboxControllerAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow, glfwJoystick);
	}

	@Override
	protected void updateValue(GLFWMappedAnalog<?> mapped, Object value) {
		super.updateValue(mapped, value);
		if (mapped == MA_LS || mapped == MA_RS) {
			Vector3f stick = (Vector3f) value;
			stick.y *= -1.0F;
		} else if (mapped == MA_LT || mapped == MA_RT) {
			Trigger1f trigger = (Trigger1f) value;
			trigger.force += 1.0F;
			trigger.force /= 2.0F;
		}
	}

}
