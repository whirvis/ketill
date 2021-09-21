package org.ardenus.engine.input.device.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ardenus.engine.input.InputException;
import org.ardenus.engine.input.device.DeviceAnalog;
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
 * @see MappedAnalog
 * @see MappedButton
 */
public abstract class DeviceAdapter<I extends InputDevice> {

	protected final Logger log;

	private final Map<Class<?>, Method> analogAdapters;
	private final Map<Class<?>, Class<?>> analogHierarchy;
	private final Map<Class<?>, Method> buttonAdapters;
	private final Map<Class<?>, Class<?>> buttonHierarchy;

	private final Map<DeviceAnalog<?>, MappedAnalog<?>> analogs;
	private final Map<DeviceButton, MappedButton> buttons;

	private final Set<DeviceAnalog<?>> missingAnalogs;
	private final Set<DeviceButton> missingButtons;

	/**
	 * Constructs a new {@code DeviceAdapter}.
	 * <p>
	 * During construction, the device adapter will register all mapped analog
	 * fields annotated with {@link AnalogMapping @AnalogMapping}. It will also
	 * register all mapped button fields annotated with
	 * {@link ButtonMapping @ButtonMapping}.
	 * 
	 * @see #map(MappedAnalog)
	 * @see #map(MappedButton)
	 * @throws InputException
	 *             if an input error occurs.
	 */
	public DeviceAdapter() {
		this.log = LogManager.getLogger(this.getClass());

		this.analogAdapters = new HashMap<>();
		this.analogHierarchy = new HashMap<>();
		this.buttonAdapters = new HashMap<>();
		this.buttonHierarchy = new HashMap<>();
		this.loadButtonAdapters();
		this.loadAnalogAdapters();

		this.analogs = new HashMap<>();
		this.buttons = new HashMap<>();
		this.loadAnalogMappings();
		this.loadButtonMappings();

		this.missingAnalogs = new HashSet<>();
		this.missingButtons = new HashSet<>();
	}

	/**
	 * Loads all predefined {@link AnalogAdapter @AnalogAdapter} annotated
	 * methods of this class instance.
	 * <p>
	 * Only instance methods will be loaded as adapters for this instance,
	 * static methods are considered invalid. The method must also be public, so
	 * that it is guaranteed to be accessible by this class. Any other access
	 * classification will make the method invalid.
	 * <p>
	 * This works by going through each field inside of this class, and checking
	 * for the presence of the {@link AnalogAdapter @AnalogAdapter} annotation.
	 * If present, the method will be added as an adapter.
	 * 
	 * @throws InputException
	 *             if a method marked with {@code @AnalogAdapter} does not
	 *             return type {@code void}; if an analog adapter method is
	 *             {@code static} or not {@code public}; if an analog adapter
	 *             method does not have exactly two parameters; if
	 *             {@code MappedAnalog} is not assignable from the first
	 *             parameter of an analog adapter method.
	 */
	private void loadAnalogAdapters() {
		for (Method method : this.getClass().getDeclaredMethods()) {
			AnalogAdapter adapter = method.getAnnotation(AnalogAdapter.class);
			if (adapter == null) {
				continue;
			}

			/*
			 * Analog adapters update an already existing value, they do not
			 * return a new one. This is to cut down on object allocations. Do
			 * not ignore this issue, and throw an exception. It is likely this
			 * was a silly mistake by the programmer.
			 */
			if (method.getReturnType() != void.class) {
				throw new InputException("expecting void return type");
			}

			/*
			 * For simplicity's sake, all analog adapters cannot be static, and
			 * they must be public. This ensures they behave like instanced
			 * methods are accessible by this class.
			 */
			int mod = method.getModifiers();
			if (Modifier.isStatic(mod)) {
				throw new InputException("analog adapter cannot be static");
			} else if (!Modifier.isPublic(mod)) {
				throw new InputException("analog adapter must be public");
			}

			/*
			 * Analog adapter methods take two parameters. The first parameter
			 * is the analog they are going to update. The second parameter is
			 * the current value of the analog, which the method updates.
			 */
			Parameter[] params = method.getParameters();
			if (params.length != 2) {
				throw new InputException("expected two parameters");
			}

			/*
			 * The first parameter must be of type MappedAnalog. Do not ignore
			 * this issue, and throw an exception. It is likely this was a silly
			 * mistake by the programmer.
			 */
			Parameter analogParam = params[0];
			if (!MappedAnalog.class.isAssignableFrom(analogParam.getType())) {
				throw new InputException("expected type assignable from"
						+ " MappedAnalog for first parameter");
			}

			Class<?> type = analogParam.getType();
			if (analogAdapters.containsKey(type)) {
				throw new InputException("duplicate adapter");
			}
			analogAdapters.put(type, method);
		}

		/*
		 * At runtime, the appropriate analog adapter method is chosen based on
		 * the highest order class assignable from the analog adapter type. If
		 * two or more classes are assignable to the same analog adapter, the
		 * device will not know which one to choose.
		 */
		for (Class<?> clazz : analogAdapters.keySet()) {
			for (Class<?> lower : analogAdapters.keySet()) {
				if (clazz != lower && clazz.isAssignableFrom(lower)) {
					throw new InputException("tangled hierarchy");
				}
			}
		}
	}

	/**
	 * Loads all predefined {@link ButtonAdapter @ButtonAdapter} annotated
	 * methods of this class instance.
	 * <p>
	 * Only instance methods will be loaded as adapters for this instance,
	 * static methods are considered invalid. The method must also be public, so
	 * that it is guaranteed to be accessible by this class. Any other access
	 * classification will make the method invalid.
	 * <p>
	 * This works by going through each field inside of this class, and checking
	 * for the presence of the {@link ButtonAdapter @ButtonAdapter} annotation.
	 * If present, the method will be added as an adapter.
	 * 
	 * @throws InputException
	 *             if a method marked with {@code @ButtonAdapter} does not
	 *             return type {@code void}; if an button adapter method is
	 *             {@code static} or not {@code public}; if an button adapter
	 *             method does not have exactly two parameters; if
	 *             {@code MappedButton} is not assignable from the first
	 *             parameter of an button adapter method.
	 */
	private void loadButtonAdapters() {
		for (Method method : this.getClass().getDeclaredMethods()) {
			ButtonAdapter adapter = method.getAnnotation(ButtonAdapter.class);
			if (adapter == null) {
				continue;
			}

			/*
			 * Button adapters return whether or not the button is pressed as a
			 * boolean. Do not ignore this issue, and throw an exception. It is
			 * likely this was a silly mistake by the programmer.
			 */
			if (method.getReturnType() != boolean.class) {
				throw new InputException("expecting boolean return type");
			}

			/*
			 * For simplicity's sake, all analog adapters cannot be static, and
			 * they must be public. This ensures they behave like instanced
			 * methods are accessible by this class.
			 */
			int mod = method.getModifiers();
			if (Modifier.isStatic(mod)) {
				throw new InputException("button adapter cannot be static");
			} else if (!Modifier.isPublic(mod)) {
				throw new InputException("button adapter must be public");
			}

			/*
			 * Buttons adapter methods take only one parameters. This single
			 * parameter is the button whose state they are checking.
			 */
			Parameter[] params = method.getParameters();
			if (params.length != 1) {
				throw new InputException("expected one parameter");
			}

			/*
			 * The first parameter must be of type MappedButton. Do not ignore
			 * this issue, and throw an exception. It is likely this was a silly
			 * mistake by the programmer.
			 */
			Parameter analogParam = params[0];
			if (!MappedButton.class.isAssignableFrom(analogParam.getType())) {
				throw new InputException("expected type assignable from"
						+ " MappedButton for parameter");
			}

			Class<?> type = analogParam.getType();
			if (buttonAdapters.containsKey(type)) {
				throw new InputException("duplicate adapter");
			}
			buttonAdapters.put(type, method);
		}

		/*
		 * At runtime, the appropriate analog adapter method is chosen based on
		 * the highest order class assignable from the analog adapter type. If
		 * two or more classes are assignable to the same analog adapter, the
		 * device will not know which one to choose.
		 */
		for (Class<?> clazz : buttonAdapters.keySet()) {
			for (Class<?> lower : buttonAdapters.keySet()) {
				if (clazz != lower && clazz.isAssignableFrom(lower)) {
					throw new InputException("tangled hierarchy");
				}
			}
		}
	}

	/**
	 * Returns all registered analog mappings.
	 * 
	 * @return all registered analog mappings.
	 */
	public Collection<MappedAnalog<?>> analogMappings() {
		return Collections.unmodifiableCollection(analogs.values());
	}

	/**
	 * Returns if this device adapter has a mapping for an analog.
	 * 
	 * @param analog
	 *            the analog to check for.
	 * @return {@code true} if {@code analog} has been mapped, {@code false}
	 *         otherwise.
	 */
	public boolean hasMapping(DeviceAnalog<?> analog) {
		return analog != null ? analogs.containsKey(analog) : false;
	}

	/**
	 * Registers an analog mapping to this adapter.
	 * 
	 * @param mapping
	 *            the mapping to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mapping} is {@code null}.
	 */
	public DeviceAdapter<I> map(MappedAnalog<?> mapping) {
		Objects.requireNonNull(mapping, "mapping");
		analogs.put(mapping.analog, mapping);

		/*
		 * Search for an appropriate analog adapter method and cache it for
		 * later calls. This will be used by updateAnalog() later. The first
		 * analog adapter method whose type is assignable from the mapped analog
		 * will be chosen. It is guaranteed by loadAnalogAdapters() that there
		 * will only be one class assignable to the mapped analog.
		 */
		Class<?> mappingClazz = mapping.getClass();
		if (!analogHierarchy.containsKey(mappingClazz)) {
			for (Class<?> candidate : analogAdapters.keySet()) {
				if (candidate.isAssignableFrom(mappingClazz)) {
					analogHierarchy.put(mappingClazz, candidate);
					break;
				}
			}

			if (!analogHierarchy.containsKey(mappingClazz)) {
				throw new InputException("unsupported analog type");
			}
		}

		return this;
	}

	/**
	 * Registers the specified analog mappings to this adapter.
	 * <p>
	 * This method is a shorthand for {@link #map(A)}, with each value of
	 * {@code mappings} being passed as the argument for {@code mapping}.
	 * 
	 * @param mappings
	 *            the mappings to register.
	 * @return this device adapter.
	 * @throws NullPointerException
	 *             if {@code mappings} or one of its elements are {@code null}.
	 */
	public DeviceAdapter<I> map(MappedAnalog<?>... mappings) {
		Objects.requireNonNull(mappings, "mappings");
		for (MappedAnalog<?> mapping : mappings) {
			this.map(mapping);
		}
		return this;
	}

	/**
	 * Unregisters the mapping for an analog from this adapter.
	 * 
	 * @param button
	 *            the analog whose mapping to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(DeviceAnalog<?> analog) {
		if (analog != null) {
			analogs.remove(analog);
		}
		return this;
	}

	/**
	 * Unregistered the mappings for the specified analogs from this adapter.
	 * <p>
	 * This method is a shorthand for {@link #unmap(DeviceAnalog)}, with each
	 * value of {@code analogs} being passed as the argument for {@code analog}.
	 * 
	 * @param analogs
	 *            the analogs whose mappings to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(DeviceAnalog<?>... analogs) {
		if (analogs != null) {
			for (DeviceAnalog<?> analog : analogs) {
				this.unmap(analog);
			}
		}
		return this;
	}

	/**
	 * Unregisters an analog mapping from this adapter.
	 * 
	 * @param mapping
	 *            the mapping to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(MappedAnalog<?> mapping) {
		Iterator<MappedAnalog<?>> buttonsI = analogs.values().iterator();
		while (buttonsI.hasNext()) {
			MappedAnalog<?> value = buttonsI.next();
			if (mapping == value) {
				buttonsI.remove();
			}
		}
		return this;
	}

	/**
	 * Unregistered the specified mappings from this adapter.
	 * <p>
	 * This method is a shorthand for {@link #unmap(MappedAnalog)}, with each
	 * value of {@code mappings} being passed as the argument for
	 * {@code mapping}.
	 * 
	 * @param mappings
	 *            the mappings to unregister.
	 * @return this device adapter.
	 */
	public DeviceAdapter<I> unmap(MappedAnalog<?>... mappings) {
		if (mappings != null) {
			for (MappedAnalog<?> mapping : mappings) {
				this.unmap(mapping);
			}
		}
		return this;
	}

	/**
	 * Loads all predefined {@link AnalogMapping @AnalogMapping} annotated
	 * fields of this class instance.<br>
	 * Both static and instance fields will be loaded as mappings for this
	 * instance.
	 * <p>
	 * This works by going through each field inside of this class, and checking
	 * for the presence of the {@link AnalogMapping @AnalogMapping} annotation.
	 * If present, the value of the field will be added as a mapping.
	 * 
	 * @throws InputException
	 *             if a field marked with {@code @AnalogMapping} is not of type
	 *             {@code MappedAnalog} or a type assignable from it; if a field
	 *             marked with {@code @AnalogMapping} is not accessible to this
	 *             class; if accessing a field fails; if multiple mappings map
	 *             to the same analog.
	 */
	private void loadAnalogMappings() {
		for (Field field : this.getClass().getDeclaredFields()) {
			AnalogMapping mapping = field.getAnnotation(AnalogMapping.class);
			if (mapping == null) {
				continue;
			}

			/*
			 * All analog mappings must be of a type extending MappedAnalog. Any
			 * other type will not contain the data necessary to map the input
			 * data. Do not ignore this issue, and throw an exception. It is
			 * likely this was a silly mistake by the programmer.
			 */
			if (!MappedAnalog.class.isAssignableFrom(field.getType())) {
				throw new InputException("expecting field type assignable "
						+ "from MappedAnalog for @AnalogMapping "
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
			 * If the method is not accessible, temporarily grant access so the
			 * contents can be retrieved. Accessibility will be reverted to its
			 * original state later.
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
				MappedAnalog<?> mapped = (MappedAnalog<?>) mappedObj;
				if (this.hasMapping(mapped.analog)) {
					throw new InputException("analog already mapped");
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
	 * @throws InputException
	 *             if no adapter exists for {@code mapping}.
	 * @see ButtonAdapter
	 */
	public DeviceAdapter<I> map(MappedButton mapping) {
		Objects.requireNonNull(mapping, "mapping");
		buttons.put(mapping.button, mapping);

		/*
		 * Search for an appropriate button adapter method and cache it for
		 * later calls. This will be used by isPressed() later. The first button
		 * adapter method whose type is assignable from the mapped button will
		 * be chosen. It is guaranteed by loadButtonAdapters() that there will
		 * only be one class assignable to the mapped analog.
		 */
		Class<?> mappingClazz = mapping.getClass();
		if (!buttonHierarchy.containsKey(mappingClazz)) {
			for (Class<?> candidate : buttonAdapters.keySet()) {
				if (candidate.isAssignableFrom(mappingClazz)) {
					buttonHierarchy.put(mappingClazz, candidate);
					break;
				}
			}

			if (!buttonHierarchy.containsKey(mappingClazz)) {
				throw new InputException("unsupported button type");
			}
		}

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
	 * @throws InputException
	 *             if no adapter exists for {@code mappings}.
	 * @see ButtonAdapter
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
	 * fields of this class instance.<br>
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
	private void loadButtonMappings() {
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
			 * If the field is not accessible, temporarily grant access so the
			 * contents can be retrieved. Accessibility will be reverted to its
			 * original state later.
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
	 * Queries the device for the current value of an analog.
	 * <p>
	 * For this method to work properly, {@code analog} must have been given a
	 * mapping as well as an appropriate adapter at construction. If no mapping
	 * was specified, this method will be a no-op. If no adapter for the
	 * analog's mapping type was registered, an {@code InputException} will be
	 * thrown.
	 * 
	 * @param analog
	 *            the analog to query.
	 * @param value
	 *            the value container to update.
	 * @throws NullPointerException
	 *             if {@code analog} or {@code value} are {@code null}.
	 * @throws InputException
	 *             if the mapping type for {@code analog} has no adapter; if
	 *             {@code value} is of an invalid type for the adapter.
	 * @see AnalogAdapter
	 * @see #map(MappedAnalog)
	 */
	public void updateAnalog(DeviceAnalog<?> analog, Object value) {
		Objects.requireNonNull(analog, "analog");
		Objects.requireNonNull(value, "value");
		MappedAnalog<?> mapping = analogs.get(analog);
		if (mapping == null) {
			if (!missingAnalogs.contains(analog)) {
				log.error("no mapping for analog \"" + analog.name + "\"");
				missingAnalogs.add(analog);
			}
			return;
		}

		Class<?> adapterClazz = analogHierarchy.get(mapping.getClass());
		Method adapter = analogAdapters.get(adapterClazz);
		try {
			adapter.invoke(this, mapping, value);
		} catch (IllegalArgumentException e) {
			/*
			 * If the value parameter type is not assignable from the passed
			 * value type, that means the IllegalArgumentException was likely
			 * caused by that. Otherwise, it was caused by something else.
			 */
			Parameter valueParam = adapter.getParameters()[1];
			if (valueParam.getType().isAssignableFrom(value.getClass())) {
				throw new InputException("invalid value type for adapter", e);
			} else {
				throw e;
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Queries the device to check if a button is pressed.
	 * <p>
	 * For this method to work properly, {@code button} must have been given a
	 * mapping as well as an appropriate adapter at construction. If no mapping
	 * was specified, {@code false} will always be returned. If no adapter for
	 * the button's mapping type was registered, an {@code InputException} will
	 * be thrown.
	 * 
	 * @param button
	 *            the button to check.
	 * @return {@code true} if {@code button} is currently pressed,
	 *         {@code false} otherwise (or {@code button} has no mapping).
	 * @throws NullPointerException
	 *             if {@code button} is {@code null}.
	 * @throws InputException
	 *             if the mapping type for {@code button} has no adapter.
	 * @see ButtonAdapter
	 * @see #map(MappedButton)
	 */
	public boolean isPressed(DeviceButton button) {
		Objects.requireNonNull(button, "button");
		MappedButton mapping = buttons.get(button);
		if (mapping == null) {
			if (!missingButtons.contains(button)) {
				log.error("no mapping for button \"" + button.name + "\"");
				missingButtons.add(button);
			}
			return false;
		}

		Class<?> adapterClazz = buttonHierarchy.get(mapping.getClass());
		Method adapter = buttonAdapters.get(adapterClazz);
		try {
			return (boolean) adapter.invoke(this, mapping);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
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
