package org.ardenus.engine.input.device.seeker;

import static org.lwjgl.glfw.GLFW.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.ardenus.engine.input.device.InputDevice;
import org.ardenus.engine.input.device.adapter.glfw.GLFWDeviceAdapter;

/**
 * A device seeker for devices using GLFW.
 */
public abstract class GLFWDeviceSeeker extends DeviceSeeker {

	private final long ptr_glfwWindow;
	private final InputDevice[] joysticks;
	private final Set<String> names;

	/**
	 * Constructs a new {@code GLFWDeviceSeeker}.
	 * 
	 * @param type
	 *            the controller type.
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param names
	 *            an initial list of qualifying joystick names.
	 * @see #addNames(String)
	 * @throws NullPointerException
	 *             if {@code type} or {@code names} is {@code null}.
	 */
	public GLFWDeviceSeeker(Class<? extends InputDevice> type,
			long ptr_glfwWindow, String... names) {
		super(type);
		this.ptr_glfwWindow = ptr_glfwWindow;
		this.joysticks = new InputDevice[GLFW_JOYSTICK_LAST];

		this.names = new HashSet<>();
		this.addNames(names);
	}

	/**
	 * Adds a qualifying joystick name.
	 * <p>
	 * When a joystick is detected by this seeker, the seeker will check its
	 * name to see if it should be connected. This is to prevent different types
	 * of joysticks from being unintentionally connected by this seeker.
	 * 
	 * @param name
	 *            the joystick name, case sensitive.
	 * @return this device seeker.
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}.
	 */
	protected GLFWDeviceSeeker addName(String name) {
		Objects.requireNonNull(name, "name");
		names.add(name);
		return this;
	}

	/**
	 * Adds the specified qualifying joystick names.
	 * <p>
	 * When a joystick is detected by this seeker, the seeker will check its
	 * name to see if it should be connected. This is to prevent different types
	 * of joysticks from being unintentionally connected by this seeker.
	 * <p>
	 * This method is a shorthand for {@link #addName(String)}, with each
	 * element of {@code names} being passed as the argument for {@code name}.
	 * 
	 * @param names
	 *            the joystick names, case sensitive.
	 * @return this device seeker.
	 * @throws NullPointerException
	 *             if {@code names} is {@code null}.
	 */
	protected GLFWDeviceSeeker addNames(String... names) {
		Objects.requireNonNull(names, "names");
		for (String name : names) {
			this.addName(name);
		}
		return this;
	}

	/**
	 * Creates a device to be connected.
	 * <p>
	 * This method is called when a qualifying joystick has been detected. The
	 * purpose of this method is to return an input device representing that
	 * joystick. The {@code ptr_glfwWindow} and {@code glfwJoystick} parameters
	 * are provided to aid in this task. These can be used to construct an
	 * instance of a {@link GLFWDeviceAdapter} for the input device.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 * @return the created input device.
	 */
	protected abstract InputDevice createDevice(long ptr_glfwWindow,
			int glfwJoystick);

	@Override
	public void seek() {
		for (int i = 0; i < joysticks.length; i++) {
			InputDevice joystick = joysticks[i];
			if (joystick != null) {
				if (!joystick.isConnected()) {
					this.unregister(joystick);
					this.joysticks[i] = null;
				}
				continue;
			}

			/*
			 * If the joystick is not present, glfwGetJoystickName() will return
			 * null. Making use of this fact gets rid of a redundant call to
			 * glfwJoystickPresent().
			 */
			String name = glfwGetJoystickName(i);
			if (name != null && names.contains(name)) {
				this.joysticks[i] = this.createDevice(ptr_glfwWindow, i);
				this.register(joysticks[i]);
			}
		}
	}

}
