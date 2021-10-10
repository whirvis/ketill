package org.ardenus.engine.input.device.adapter.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.feature.Button1b;
import org.ardenus.engine.input.device.feature.Trigger1f;
import org.joml.Vector3f;

/**
 * An adapter which maps input for a GLFW joystick.
 *
 * @param <I>
 *            the input device type.
 * @see GLFWDeviceAdapter
 * @see GLFWMappedAnalog
 * @see GLFWButtonMapping
 */
public abstract class GLFWJoystickAdapter<I extends InputDevice>
		extends GLFWDeviceAdapter<I> {

	protected final int glfwJoystick;
	protected FloatBuffer axes;
	protected ByteBuffer buttons;

	/**
	 * Constructs a new {@code GLFWJoystickAdapter}.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 * @throws InputException
	 *             if an input error occurs.
	 * @see #map(AnalogMapping)
	 * @see #map(ButtonMapping)
	 */
	public GLFWJoystickAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow);
		this.glfwJoystick = glfwJoystick;
	}

	protected boolean isPressed(GLFWAnalogStickMapping mapping) {
		GLFWButtonMapping zMapping =
				(GLFWButtonMapping) this.getMapping(mapping.feature.zButton);
		if (zMapping == null) {
			return false;
		}
		return buttons.get(zMapping.glfwButton) != 0;
	}

	@Override
	public boolean isConnected() {
		return glfwJoystickPresent(glfwJoystick);
	}

	@FeatureAdapter
	public void updateAnalogStick(GLFWAnalogStickMapping mapping,
			Vector3f stick) {
		stick.x = axes.get(mapping.glfwAxisX);
		stick.y = axes.get(mapping.glfwAxisY);
		stick.z = this.isPressed(mapping) ? -1.0F : 0.0F;
	}

	@FeatureAdapter
	public void updateAnalogTrigger(GLFWAnalogTriggerMapping mapping,
			Trigger1f trigger) {
		trigger.force = axes.get(mapping.glfwAxis);
	}

	@FeatureAdapter
	public void isPressed(GLFWButtonMapping mapped, Button1b state) {
		state.pressed = buttons.get(mapped.glfwButton) != 0;
	}

	@Override
	public void poll() {
		this.axes = glfwGetJoystickAxes(glfwJoystick);
		this.buttons = glfwGetJoystickButtons(glfwJoystick);
	}

}
