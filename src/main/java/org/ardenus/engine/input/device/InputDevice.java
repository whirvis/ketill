package org.ardenus.engine.input.device;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.ardenus.engine.input.Analog;
import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.adapter.DeviceAdapter;
import org.ardenus.engine.input.button.PressableState;

/**
 * A device which can send and receive input data.
 * <p>
 * Examples of input devices include, but are not limited to: keyboards, mouses,
 * XBOX controllers, PlayStation controllers, etc. By default, an input device
 * only has support for buttons. However, depending on the implementation,
 * features like mouse coordinates, gyroscopes, etc. may be present.
 * <p>
 * <b>Note:</b> For an input device to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the device once on every application update.
 * 
 * @see DeviceAdapter
 */
public abstract class InputDevice {

	protected final DeviceAdapter<?> adapter;
	private final Map<DeviceAnalog<?>, Object> analogs;
	private final Map<DeviceButton, PressableState> buttons;

	/**
	 * Constructs a new {@code InputDevice} and registers all device button
	 * fields annotated with {@link ButtonPresent @ButtonPresent}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @see #addButton(DeviceButton)
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 * @throws InputDevice
	 *             if an input error occurs.
	 */
	public InputDevice(DeviceAdapter<?> adapter) {
		this.adapter = Objects.requireNonNull(adapter);
		this.analogs = new HashMap<>();
		this.buttons = new HashMap<>();
		this.loadAnalogs();
		this.loadButtons();
	}

	/**
	 * Returns the analogs registered to this input device.
	 * 
	 * @return the analogs registered to this input device.
	 */
	public Set<Analog<?>> getAnalogs() {
		return Collections.unmodifiableSet(analogs.keySet());
	}

	/**
	 * Returns if an analog is registered to this input device.
	 * 
	 * @param analog
	 *            the analog to check for.
	 * @return {@code true} if {@code analog} is registered, {@code false}
	 *         otherwise.
	 */
	public boolean hasAnalag(DeviceAnalog<?> analog) {
		return analog != null ? analogs.containsKey(analog) : false;
	}

	/**
	 * Registers an analog to this input device.
	 * <p>
	 * When an analog is registered, it is stored alongside an instance of the
	 * position container it creates. This position will be updated as needed
	 * each time the controller is polled. If {@code analog} is already
	 * registered, its current value will not be reset.
	 * <p>
	 * <b>Note:</b> This method can be called before {@code InputDevice} is
	 * finished constructing, as it is called by the {@link #loadAnalogs()}
	 * method (which is called inside the constructor). As such, extending
	 * classes should take care to write code around this fact should they
	 * override this method.
	 * 
	 * @param analog
	 *            the analog to register.
	 * @return this input device.
	 * @throws NullPointerException
	 *             if {@code analog} is {@code null}.
	 */
	protected InputDevice addAnalog(DeviceAnalog<?> analog) {
		Objects.requireNonNull(analog);
		if (!analogs.containsKey(analog)) {
			analogs.put(analog, analog.zero());
		}
		return this;
	}

	/**
	 * Registers all {@link AnalogPresent @AnalogPresent} annotated device
	 * analogs present in this class to this input device.
	 * <p>
	 * This works by going through each field inside of this class, and checking
	 * for the presence of the {@link AnalogPresent @AnalogPresent} annotation.
	 * If present, the value of the field will be fetched and registered via
	 * {@link #addAnalog(DeviceAnalog)}.
	 * 
	 * @throws InputException
	 *             if a field marked with {@code @AnalogPresent} is not of type
	 *             {@code Analog} or a type assignable from it; if a field
	 *             marked with {@code @AnalogPresent} is not accessible to this
	 *             class; if accessing a field fails.
	 */
	private void loadAnalogs() {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(AnalogPresent.class)) {
				continue;
			}

			/*
			 * All analog mappings must be of a type extending DeviceAnalog. Any
			 * other type will not contain the data necessary to map the input
			 * data. Do not ignore this issue, and throw an exception. It is
			 * likely this was a silly mistake by the programmer.
			 */
			if (!DeviceAnalog.class.isAssignableFrom(field.getType())) {
				throw new InputException("expecting field type assignable "
						+ "from DeviceAnalog for @AnalogPresent "
						+ field.getName());
			}

			/*
			 * We must know if the field is static before trying to get its
			 * value. If it is static, we must pass null for the instance.
			 * Otherwise, we must pass this current device instance.
			 */
			int modifiers = field.getModifiers();
			boolean statik = Modifier.isStatic(modifiers);

			/*
			 * Sometimes we'll encounter one of those spooky "private" variables
			 * or something like that. If it's not accessible to us, temporarily
			 * grant ourselves access to retrieve the field's contents. We'll
			 * revert the accessibility back to its original state later.
			 */
			boolean tempAccess = false;
			if (!field.canAccess(statik ? null : this)) {
				try {
					field.setAccessible(true);
					tempAccess = true;
				} catch (InaccessibleObjectException | SecurityException e) {
					throw new InputException("failure to set accessible", e);
				}
			}

			try {
				Object analog = field.get(statik ? null : this);
				this.addAnalog((DeviceAnalog<?>) analog);
			} catch (IllegalAccessException e) {
				throw new InputException("failure to access", e);
			} finally {
				if (tempAccess) {
					field.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Returns the current value of an analog.
	 * 
	 * @param <V>
	 *            the value type of {@code analog}.
	 * @param analog
	 *            the analog whose value to get.
	 * @return the current value of {@code analog}.
	 */
	public <V> V getValue(DeviceAnalog<V> analog) {
		if (analog == null) {
			return null;
		}
		Object value = analogs.get(analog);
		return analog.cast(value);
	}

	/**
	 * Returns the buttons registered to this input device.
	 * 
	 * @return the buttons registered to this input device.
	 */
	public Set<DeviceButton> getButtons() {
		return Collections.unmodifiableSet(buttons.keySet());
	}

	/**
	 * Returns if a button is registered to this input device.
	 * 
	 * @param button
	 *            the button to check for.
	 * @return {@code true} if {@code button} is registered, {@code false}
	 *         otherwise.
	 */
	public boolean hasButton(DeviceButton button) {
		if (button != null) {
			return buttons.containsKey(button);
		}
		return false;
	}

	/**
	 * Registers a button to this input device.
	 * <p>
	 * When a button is registered, it is stored alongside an instance of
	 * {@link PressableState} to track its current state. If {@code button} is
	 * already registered, its current state will not be reset.
	 * <p>
	 * <b>Note:</b> This method can be called before {@code InputDevice} is
	 * finished constructing, as it is called by the {@link #loadButtons()}
	 * method (which is called inside the constructor). As such, extending
	 * classes should take care to write code around this fact should they
	 * override this method.
	 * 
	 * @param button
	 *            the button to register.
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 */
	protected void addButton(DeviceButton button) {
		Objects.requireNonNull(button, "button");
		if (!buttons.containsKey(button)) {
			buttons.put(button, new PressableState());
		}
	}

	/**
	 * Registers all {@link ButtonPresent @ButtonPresent} annotated device
	 * buttons present in this class to this input device.
	 * <p>
	 * This works by going through each field inside of this class, and checking
	 * for the presence of the {@link ButtonPressent @ButtonPresent} annotation.
	 * If present, the value of the field will be fetched and registered via
	 * {@link #addButton(DeviceButton)}.
	 * 
	 * @throws InputException
	 *             if a field marked with {@code @ButtonPresent} is not of type
	 *             {@code DeviceButton} or a type assignable from it; if a field
	 *             marked with {@code @ButtonPresent} is not accessible to this
	 *             class; if accessing a field fails.
	 */
	private void loadButtons() {
		for (Field field : this.getClass().getDeclaredFields()) {
			if (!field.isAnnotationPresent(ButtonPresent.class)) {
				continue;
			}

			/*
			 * All button mappings must be of a type extending DeviceButton. Any
			 * other type will not contain the data necessary to map the input
			 * data. Do not ignore this issue, and throw an exception. It is
			 * likely this was a silly mistake by the programmer.
			 */
			if (!DeviceButton.class.isAssignableFrom(field.getType())) {
				throw new InputException("expecting field type assignable "
						+ "from DeviceButton for @ButtonPresent "
						+ field.getName());
			}

			/*
			 * We must know if the field is static before trying to get its
			 * value. If it is static, we must pass null for the instance.
			 * Otherwise, we must pass this current device instance.
			 */
			int modifiers = field.getModifiers();
			boolean statik = Modifier.isStatic(modifiers);

			/*
			 * Sometimes we'll encounter one of those spooky "private" variables
			 * or something like that. If it's not accessible to us, temporarily
			 * grant ourselves access to retrieve the field's contents. We'll
			 * revert the accessibility back to its original state later.
			 */
			boolean tempAccess = false;
			if (!field.canAccess(statik ? null : this)) {
				try {
					field.setAccessible(true);
					tempAccess = true;
				} catch (InaccessibleObjectException | SecurityException e) {
					throw new InputException("failure to set accessible", e);
				}
			}

			try {
				Object button = field.get(statik ? null : this);
				this.addButton((DeviceButton) button);
			} catch (IllegalAccessException e) {
				throw new InputException("failure to access", e);
			} finally {
				if (tempAccess) {
					field.setAccessible(false);
				}
			}
		}
	}

	/**
	 * Returns the current state of a button.
	 * 
	 * @param button
	 *            the button whose state to get.
	 * @return the current state of {@code button}, {@code null} if this input
	 *         device has no such button registered.
	 */
	protected PressableState getState(DeviceButton button) {
		return button != null ? buttons.get(button) : null;
	}

	/**
	 * Returns if a button is currently pressed.
	 * <p>
	 * Whether or not a button is considered to be pressed is based on the
	 * definition of {@link PressableState#isPressed()}. This method is more or
	 * less a shorthand for it.
	 * 
	 * @param button
	 *            the button whose state to check.
	 * @return {@code true} if {@code button} is currently pressed,
	 *         {@code false} otherwise.
	 */
	public boolean isPressed(DeviceButton button) {
		PressableState state = this.getState(button);
		return state != null ? state.isPressed() : false;
	}

	/**
	 * Returns if a button is currently held down.
	 * <p>
	 * Whether or not a button is considered to be held down is based on the
	 * definition of {@link PressableState#isHeld()}. This method is more or
	 * less a shorthand for it.
	 * 
	 * @param button
	 *            the button whose state to check.
	 * @return {@code true} if {@code button} is currently held down,
	 *         {@code false} otherwise.
	 */
	public boolean isHeld(DeviceButton button) {
		PressableState state = this.getState(button);
		return state != null ? state.isHeld() : false;
	}

	/**
	 * Polls this input device.
	 * <p>
	 * Polling an input device is necessary for retrieving up to date input
	 * information. By default, only the states of registered analogs and
	 * buttons will be updated. However, more information may be polled
	 * depending on the implementation.
	 */
	public void poll() {
		adapter.poll();

		for (DeviceAnalog<?> analog : analogs.keySet()) {
			analogs.put(analog, adapter.getValue(analog));
		}

		for (DeviceButton button : buttons.keySet()) {
			PressableState state = buttons.get(button);
			state.cache();
			state.setPressed(adapter.isPressed(button));
			state.update();
		}
	}

}
