package org.ardenus.engine.input.device.adapter.glfw.joystick;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.DeviceButton;
import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.MappedAnalog;
import org.ardenus.engine.input.device.adapter.MappedButton;
import org.ardenus.engine.input.device.adapter.glfw.GLFWDeviceAdapter;
import org.ardenus.engine.input.device.adapter.glfw.GLFWMappedButton;
import org.ardenus.engine.input.device.adapter.glfw.analog.GLFWMappedAnalog;
import org.ardenus.engine.input.device.adapter.glfw.analog.GLFWMappedAnalogStick;
import org.ardenus.engine.input.device.adapter.glfw.analog.GLFWMappedAnalogTrigger;
import org.ardenus.engine.input.device.analog.Trigger1f;
import org.joml.Vector3f;

/**
 * An adapter which maps input for a GLFW joystick.
 *
 * @param <I>
 *            the input device type.
 * @param <A>
 *            the analog input type.
 * @param <B>
 *            the button type.
 * @see GLFWDeviceAdapter
 * @see GLFWMappedAnalog
 * @see GLFWMappedButton
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
	 * @see #map(MappedAnalog)
	 * @see #map(MappedButton)
	 */
	public GLFWJoystickAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow);
		this.glfwJoystick = glfwJoystick;
	}

	@Override
	public boolean isConnected() {
		return glfwJoystickPresent(glfwJoystick);
	}

	@Override
	protected void updateValue(MappedAnalog<?> mapped, Object value) {
		if (mapped instanceof GLFWMappedAnalogStick) {
			GLFWMappedAnalogStick mappedStick = (GLFWMappedAnalogStick) mapped;
			DeviceButton zButton = mappedStick.analog.zButton;

			Vector3f stick = (Vector3f) value;
			stick.x = axes.get(mappedStick.glfwAxisX);
			stick.y = axes.get(mappedStick.glfwAxisY);
			stick.z = this.isPressed(zButton) ? -1.0F : 0.0F;
		} else if (mapped instanceof GLFWMappedAnalogTrigger) {
			GLFWMappedAnalogTrigger mappedTrigger =
					(GLFWMappedAnalogTrigger) mapped;

			Trigger1f trigger = (Trigger1f) value;
			trigger.force = axes.get(mappedTrigger.glfwAxis);
		} else {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	protected boolean isPressed(MappedButton mapped) {
		GLFWMappedButton glfwMapped = (GLFWMappedButton) mapped;
		return buttons.get(glfwMapped.glfwButton) > 0;
	}

	@Override
	public void poll() {
		this.axes = glfwGetJoystickAxes(glfwJoystick);
		this.buttons = glfwGetJoystickButtons(glfwJoystick);
	}

}
