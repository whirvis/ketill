package org.ardenus.input.adapter.glfw;

import static org.lwjgl.glfw.GLFW.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.ardenus.input.InputDevice;
import org.ardenus.input.adapter.FeatureAdapter;
import org.ardenus.input.feature.Button1b;
import org.ardenus.input.feature.DeviceButton;
import org.ardenus.input.feature.Trigger1f;
import org.joml.Vector3f;

/**
 * An adapter which maps input for a GLFW joystick.
 *
 * @param <I>
 *            the input device type.
 * @see GlfwButtonMapping
 * @see GlfwStickMapping
 * @see GlfwTriggerMapping
 */
public abstract class GlfwJoystickAdapter<I extends InputDevice>
		extends GlfwDeviceAdapter<I> {

	protected final int glfwJoystick;
	protected FloatBuffer axes;
	protected ByteBuffer buttons;

	/**
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 */
	public GlfwJoystickAdapter(long ptr_glfwWindow, int glfwJoystick) {
		super(ptr_glfwWindow);
		this.glfwJoystick = glfwJoystick;
	}

	protected boolean isPressed(GlfwButtonMapping mapping) {
		return buttons.get(mapping.glfwButton) != 0;
	}

	protected boolean isPressed(GlfwStickMapping mapping) {
		DeviceButton zButton = mapping.feature.zButton;
		GlfwButtonMapping zMapping =
				(GlfwButtonMapping) this.getMapping(zButton);
		return this.isPressed(zMapping);
	}

	@Override
	public boolean isConnected() {
		return glfwJoystickPresent(glfwJoystick);
	}

	@FeatureAdapter
	public void isPressed(GlfwButtonMapping mapping, Button1b button) {
		button.pressed = this.isPressed(mapping);
	}

	@FeatureAdapter
	public void updateStick(GlfwStickMapping mapping, Vector3f stick) {
		stick.x = axes.get(mapping.glfwAxisX);
		stick.y = axes.get(mapping.glfwAxisY);
		stick.z = this.isPressed(mapping) ? -1.0F : 0.0F;
	}

	@FeatureAdapter
	public void updateTrigger(GlfwTriggerMapping mapping, Trigger1f trigger) {
		trigger.force = axes.get(mapping.glfwAxis);
	}

	@Override
	public void poll() {
		this.axes = glfwGetJoystickAxes(glfwJoystick);
		this.buttons = glfwGetJoystickButtons(glfwJoystick);
	}

}
