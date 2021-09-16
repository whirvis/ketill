package org.ardenus.engine.input.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.DeviceButton;
import org.ardenus.engine.input.device.InputDevice;

/**
 * An adapter which maps input for an {@link InputDevice}.
 * <p>
 * The purpose of a device adapter is to map data from a source (such as GLFW or
 * XInput) to an input device. This allows the same input device to be used with
 * different implementations. The use of adapters brings portability while also
 * providing a way to enable extra features, such as rumble or gyroscopes.
 * <p>
 * <b>Note:</b> For a device adapter to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the adapter once on every application update.
 *
 * @param <I>
 *            the input device type.
 * @see MappedButton
 */
public abstract class DeviceAdapter<I extends InputDevice> {

	private final Map<DeviceButton, MappedButton> buttons;

	/**
	 * Constructs a new {@code DeviceAdapter} and registers all mapped button
	 * fields annotated with {@link ButtonMapping @ButtonMapping}.
	 * 
	 * @see #map(MappedButton)
	 * @throws InputDevice
	 *             if an input error occurs.
	 */
	public DeviceAdapter() {
		this.buttons = new HashMap<>();
		this.loadButtonMappings();
	}

	/**
	 * Returns all registered button mappings.
	 * 
	 * @return all registered button mappings.
	 */
	public Collection<MappedButton> buttonMappings() {
		return Collections.unmodifiableCollection(buttons.values());
	}

	/**
	 * Returns if this device adapter has a mapping for a button.
	 * 
	 * @param button
	 *            the button to check for.
	 * @return {@code true} if {@code button} has been mapped, {@code false}
	 *         otherwise.
	 */
	public boolean hasMapping(DeviceButton button) {
		return button != null ? buttons.containsKey(button) : false;
	}

	/**
	 * Registers a button mapping to this adapter.
	 * 
	 * @param mapping
	 *            the mapping to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mapping} is {@code null}.
	 */
	public DeviceAdapter<I> map(MappedButton mapping) {
		Objects.requireNonNull(mapping, "mapped");
		buttons.put(mapping.button, mapping);
		return this;
	}

	/**
	 * Registers the specified button mappings to this adapter.
	 * <p>
	 * This method is a shorthand for {@link #map(MappedButton)}, with each
	 * value of {@code mappings} being passed as the argument for
	 * {@code mapping}.
	 * 
	 * @param mappings
	 *            the mappings to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mappings} or one of its elements are {@code null}.
	 */
	public DeviceAdapter<I> map(MappedButton... mappings) {
		Objects.requireNonNull(mappings, "mappings");
		for (MappedButton mapping : mappings) {
			this.map(mapping);
		}
		return this;
	}

	/**
	 * Unregisters the mapping for a button from this adapter.
	 * 
	 * @param button
	 *            the button whose mapping to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(DeviceButton button) {
		if (button != null) {
			buttons.remove(button);
		}
		return this;
	}

	/**
	 * Unregistered the mappings for the specified buttons from this adapter.
	 * <p>
	 * This method is a shorthand for {@link #unmap(DeviceButton)}, with each
	 * value of {@code buttons} being passed as the argument for {@code button}.
	 * 
	 * @param buttons
	 *            the buttons whose mappings to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(DeviceButton... buttons) {
		if (buttons != null) {
			for (DeviceButton button : buttons) {
				this.unmap(button);
			}
		}
		return this;
	}

	/**
	 * Unregisters a button mapping from this adapter.
	 * 
	 * @param mapping
	 *            the mapping to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(MappedButton mapping) {
		Iterator<MappedButton> buttonsI = buttons.values().iterator();
		while (buttonsI.hasNext()) {
			MappedButton value = buttonsI.next();
			if (mapping == value) {
				buttonsI.remove();
			}
		}
		return this;
	}

	/**
	 * Unregistered the specified mappings from this adapter.
	 * <p>
	 * This method is a shorthand for {@link #unmap(MappedButton)}, with each
	 * value of {@code mappings} being passed as the argument for
	 * {@code mapping}.
	 * 
	 * @param mappings
	 *            the mappings to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(MappedButton... mappings) {
		if (mappings != null) {
			for (MappedButton mapping : mappings) {
				this.unmap(mapping);
			}
		}
		return this;
	}

	/**
	 * Loads all predefined {@link ButtonMapping @ButtonMapping} annotated
	 * fields of this class instance. <br>
	 * Both static and instance fields will be loaded as mappings for this
	 * instance.
	 * <p>
	 * This works by going through each field inside of this class, and checking
	 * for the presence of the {@link ButtonMapping @ButtonMapping} annotation.
	 * If present, the value of the field will be added as a mapping.
	 * 
	 * @throws InputException
	 *             if a field marked with {@code @ButtonMapping} is not of type
	 *             {@code MappedButton} or a type assignable from it; if a field
	 *             marked with {@code @ButtonMapping} is not accessible to this
	 *             class; if accessing a field fails; if multiple mappings map
	 *             to the same button.
	 */
	protected void loadButtonMappings() {
		for (Field field : this.getClass().getDeclaredFields()) {
			ButtonMapping mapping = field.getAnnotation(ButtonMapping.class);
			if (mapping == null) {
				continue;
			}

			/*
			 * All button mappings must be of a type extending MappedButton. Any
			 * other type will not contain the data necessary to map the input
			 * data. Do not ignore this issue, and throw an exception. It is
			 * likely this was a silly mistake by the programmer.
			 */
			if (!MappedButton.class.isAssignableFrom(field.getType())) {
				throw new InputException("expecting field type assignable "
						+ "from MappedButton for @ButtonMapping "
						+ field.getName());
			}

			/*
			 * We must know if the field is static before trying to get its
			 * value. If it is static, we must pass null for the instance.
			 * Otherwise, we must pass this current adapter instance.
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
				Object mappedObj = field.get(statik ? null : this);
				MappedButton mapped = (MappedButton) mappedObj;
				if (this.hasMapping(mapped.button)) {
					throw new InputException("button already mapped");
				}
				this.map(mapped);
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
	 * Returns if this device adapter is still connected.
	 * 
	 * @return {@code true} if this device adapter is still connected,
	 *         {@code false} otherwise.
	 */
	public abstract boolean isConnected();

	/**
	 * Returns if a mapped button is currently pressed.
	 * 
	 * @param mapped
	 *            the mapped button, guaranteed not to be {@code null}.
	 * @return {@code true} if {@code mapped} is pressed, {@code false}
	 *         otherwise.
	 */
	protected abstract boolean isPressed(MappedButton mapped);

	/**
	 * Returns if a button is currently pressed.
	 * <p>
	 * This method is a shorthand for abstract {@link #isPressed(DeviceButton)},
	 * with the argument for {@code mapped} being the value that {@code button}
	 * is mapped to. If there is no mapping for {@code button}, {@code false}
	 * will be returned instead.
	 * 
	 * @param button
	 *            the button.
	 * @return {@code true} if {@code button} is currently pressed,
	 *         {@code false} otherwise.
	 */
	public boolean isPressed(DeviceButton button) {
		MappedButton mapped = buttons.get(button);
		return mapped != null ? this.isPressed(mapped) : false;
	}

	/**
	 * Polls the adapter to update input device data.
	 * <p>
	 * Polling a device adapter is not always necessary for retrieving up to
	 * date input information. However, this is only down to how the adapter
	 * retrieves information from its source. As such, it is highly recommended
	 * to always poll the adapter; even if the method may be a no-op.
	 */
	public abstract void poll();

}