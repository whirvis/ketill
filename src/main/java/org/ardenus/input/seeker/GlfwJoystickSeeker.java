package org.ardenus.input.seeker;

import static org.lwjgl.glfw.GLFW.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.ardenus.input.DeviceGuids;
import org.ardenus.input.InputDevice;
import org.ardenus.input.InputException;
import org.ardenus.input.OperatingSystem;
import org.ardenus.input.adapter.glfw.GlfwDeviceAdapter;

import com.whirvex.event.EventManager;

public abstract class GlfwJoystickSeeker extends GlfwDeviceSeeker {

	public final String typeId;
	private final InputDevice[] joysticks;
	private final Set<String> guids;

	/**
	 * @param type
	 *            the joystick type.
	 * @param events
	 *            the event manager, may be {@code null}.
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @throws NullPointerException
	 *             if {@code type} is {@code null}; if {@code @DeviceId} is not
	 *             present for {@code type}.
	 * @see #seekGuid(String)
	 */
	public GlfwJoystickSeeker(Class<? extends InputDevice> type,
			EventManager events, long ptr_glfwWindow) {
		super(type, events, ptr_glfwWindow);

		this.typeId = InputDevice.getId(type);
		if (typeId == null) {
			throw new NullPointerException("type missing @DeviceId");
		}

		this.joysticks = new InputDevice[GLFW_JOYSTICK_LAST + 1];
		this.guids = new HashSet<>();
	}

	/**
	 * @param guid
	 *            the joystick GUID, case sensitive.
	 * @return {@code true} if this device seeker is seeking out joysticks with
	 *         {@code guid}, {@code false} otherwise.
	 * @see #seekGuid(String)
	 */
	public boolean isSeeking(String guid) {
		if (guid != null) {
			return guids.contains(guid);
		}
		return false;
	}

	/**
	 * When detected, the GUID of a joystick will be checked to see if the
	 * seeker should register it. This is to prevent undesired joysticks from
	 * being erroneously registered.
	 * 
	 * @param guid
	 *            the joystick GUID, case sensitive.
	 * @return this device seeker.
	 * @throws NullPointerException
	 *             if {@code guid} is {@code null}.
	 */
	public GlfwJoystickSeeker seekGuid(String guid) {
		Objects.requireNonNull(guid, "guid");
		guids.add(guid);
		return this;
	}

	/**
	 * When detected, the GUID of a joystick will be checked to see if the
	 * seeker should register it. This is to prevent undesired joysticks from
	 * being erroneously registered.
	 * <p>
	 * This method is a shorthand for {@link #seekGuid(String)}, with each
	 * element of {@code guids} being passed as the argument for {@code guid}.
	 * 
	 * @param guids
	 *            the joystick GUIDs, case sensitive.
	 * @return this device seeker.
	 * @throws NullPointerException
	 *             if {@code guids} is {@code null}.
	 */
	public GlfwJoystickSeeker seekGuids(Iterable<String> guids) {
		Objects.requireNonNull(guids, "guids");
		for (String guid : guids) {
			this.seekGuid(guid);
		}
		return this;
	}

	/**
	 * When detected, the GUID of a joystick will be checked to see if the
	 * seeker should register it. This is to prevent undesired joysticks from
	 * being erroneously registered.
	 * <p>
	 * This method is a shorthand for {@link #seekGuids(Iterable)}, with the
	 * argument for {@code guids} being converted from an array to a list via
	 * {@link Arrays#asList(Object...)}.
	 * 
	 * @param guids
	 *            the joystick GUIDs, case sensitive.
	 * @return this device seeker.
	 * @throws NullPointerException
	 *             if {@code guids} is {@code null}.
	 */
	public GlfwJoystickSeeker seekGuids(String... guids) {
		return this.seekGuids(Arrays.asList(guids));
	}

	/**
	 * When detected, the GUID of a joystick will be checked to see if the
	 * seeker should register it. This is to prevent undesired joysticks from
	 * being erroneously registered.
	 * <p>
	 * This method is a shorthand for {@link #seekGuids(Iterable)}, with the
	 * GUIDs to seek being fetched from {@code guids} using the ID of the
	 * targeted joystick and current operating system.
	 * <p>
	 * <b>Note:</b> Only the current contents {@code guids} will be fetched.
	 * 
	 * @param guids
	 *            the container of joystick GUIDs.
	 * @return this device seeker.
	 * @throws NullPointerException
	 *             if {@code guids} is {@code null}.
	 * @throws InputException
	 *             if the current OS is unknown; if no GUIDs for the targeted
	 *             joystick on the current OS are present within {@code guids}.
	 */
	public GlfwJoystickSeeker seekGuids(DeviceGuids guids) {
		Objects.requireNonNull(guids, "guids");
		OperatingSystem os = OperatingSystem.get();
		if (os == null) {
			throw new InputException("unknown OS");
		}

		Iterable<String> osGuids = guids.getGuids(typeId, os);
		if (osGuids == null) {
			throw new InputException(
					"no GUIDs for " + typeId + " on " + os.name);
		}
		return this.seekGuids(osGuids);
	}

	/**
	 * All currently registered joysticks with a matching GUID will be
	 * automatically unregistered. This is to prevent the connection of
	 * undesired joysticks from lingering.
	 * 
	 * @param toDrop
	 *            the GUIDs to drop, case sensitive.
	 * @return this device seeker.
	 */
	public GlfwJoystickSeeker dropGuids(Collection<String> toDrop) {
		/*
		 * The version using a collection (rather than a single string) is
		 * provided as original implementation, as it allows for an O(n)
		 * implementation rather than an O(n^2). Maybe this is a micro
		 * optimization, but I don't really care.
		 */
		if (toDrop == null) {
			return this;
		}

		/*
		 * If a joystick's GUID matches one of the GUIDs being dropped, it must
		 * be unregistered. It would not make logical sense for a joystick that
		 * would no longer be registered by this seeker to linger.
		 */
		for (int i = 0; i < joysticks.length; i++) {
			String guid = glfwGetJoystickGUID(i);
			if (guid != null && toDrop.contains(guid)) {
				this.unregister(joysticks[i]);
				this.joysticks[i] = null;
			}
		}

		guids.removeAll(toDrop);
		return this;
	}

	/**
	 * All currently registered joysticks with a matching GUID will be
	 * automatically unregistered. This is to prevent the connection of
	 * undesired joysticks from lingering.
	 * <p>
	 * This method is a shorthand for {@link #dropGuids(Collection)}, with the
	 * argument for {@code toDrop} being converted from an array to a list via
	 * {@link Arrays#asList(Object...)}.
	 * 
	 * @param toDrop
	 *            the GUIDs to drop, case sensitive. If the argument for this
	 *            parameter is an empty array, all GUIDs currently being sought
	 *            for will be dropped.
	 * @return this device seeker.
	 */
	public GlfwJoystickSeeker dropGuids(String... toDrop) {
		if (toDrop == null) {
			return this;
		} else if (toDrop.length == 0) {
			return this.dropGuids(guids);
		}
		return this.dropGuids(Arrays.asList(toDrop));
	}

	/**
	 * All currently registered joysticks with a matching GUID will be
	 * automatically unregistered. This is to prevent the connection of
	 * undesired joysticks from lingering.
	 * <p>
	 * This method is a shorthand for {@link #dropGuids(String...)}, with
	 * {@code toDrop} being passed as the only value.
	 * 
	 * @param toDrop
	 *            the GUID to drop, case sensitive.
	 * @return this device seeker.
	 */
	public GlfwJoystickSeeker dropGuid(String toDrop) {
		return this.dropGuids(toDrop);
	}

	/**
	 * All currently registered joysticks with a matching GUID will be
	 * automatically unregistered. This is to prevent the connection of
	 * undesired joysticks from lingering.
	 * <p>
	 * This method is a shorthand for {@link #dropGuids(Collection)}, with the
	 * GUIDs to drop being fetched from {@code toDrop} using the ID of the
	 * targeted joystick and current operating system.
	 * <p>
	 * <b>Note:</b> Only the current contents {@code guids} will be fetched.
	 * 
	 * @param toDrop
	 *            the container of GUIDs to drop.
	 * @return this device seeker.
	 */
	public GlfwJoystickSeeker dropGuids(DeviceGuids toDrop) {
		OperatingSystem os = OperatingSystem.get();
		if (toDrop == null || os == null) {
			return this;
		}

		Iterable<String> osGuids = toDrop.getGuids(typeId, os);
		if (osGuids != null) {
			/*
			 * The Iterable is converted into a set here to allow the operation
			 * to be O(2n), which cancels out to O(n). Using dropGuid(String)
			 * for each element would result in the operation becoming O(n^2).
			 */
			Set<String> osGuidsSet = new HashSet<>();
			for (String guid : osGuids) {
				osGuidsSet.add(guid);
			}
			this.dropGuids(osGuidsSet);
		}
		return this;
	}

	/**
	 * This method is called when a qualifying joystick has been detected. The
	 * purpose of this method is to return an input device representing that
	 * joystick. The {@code ptr_glfwWindow} and {@code glfwJoystick} parameters
	 * are provided to aid in this task. These can be used to construct an
	 * instance of a {@link GlfwDeviceAdapter} for the input device.
	 * 
	 * @param ptr_glfwWindow
	 *            the GLFW window pointer.
	 * @param glfwJoystick
	 *            the GLFW joystick ID.
	 * @return the created input device.
	 */
	protected abstract InputDevice createDevice(long ptr_glfwWindow,
			int glfwJoystick);

	/**
	 * @throws InputException
	 *             if no GUIDs for the targeted joystick were specified.
	 * @see #seekGuid(String)
	 */
	@Override
	public void seek() {
		if (guids.isEmpty()) {
			throw new InputException("no GUIDs specified for " + typeId);
		}

		for (int i = 0; i < joysticks.length; i++) {
			InputDevice joystick = this.joysticks[i];
			if (joystick != null) {
				if (!joystick.isConnected()) {
					this.unregister(joystick);
					this.joysticks[i] = null;
				}
				continue;
			}

			/*
			 * If the joystick is not present, glfwGetJoystickGUID() returns
			 * null. This makes a call to glfwJoystickPresent() redundant.
			 */
			String guid = glfwGetJoystickGUID(i);
			if (guid != null && this.isSeeking(guid)) {
				this.joysticks[i] = this.createDevice(ptr_glfwWindow, i);
				this.register(joysticks[i]);
			}
		}
	}

}
