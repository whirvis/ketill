package com.whirvis.kibasan.xinput;

import com.github.strikerx3.jxinput.XInputButtons;
import com.whirvis.controller.ButtonMapping;
import com.whirvis.controller.DeviceButton;

import java.lang.reflect.Field;
import java.util.Objects;

public class XButtonMapping extends ButtonMapping {

	public final String fieldName;
	private final Field field;

	/**
	 * @param button
	 *            the button being mapped to.
	 * @param fieldName
	 *            the name of the field that this button maps to in the
	 *            {@code XInputButtons} class.
	 * @throws NullPointerException
	 *             if {@code button} or {@code fieldName} are {@code null}.
	 * @throws IllegalArgumentException
	 *             if no field in {@code XInputButtons} with name
	 *             {@code fieldName} exists or is accessible to this class.
	 */
	public XButtonMapping(DeviceButton button, String fieldName) {
		super(button);
		this.fieldName = Objects.requireNonNull(fieldName, "fieldName");
		try {
			this.field = XInputButtons.class.getField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException("no such button", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("field not accessible", e);
		}
	}

	/**
	 * @param buttons
	 *            the X-input buttons state.
	 * @return {@code true} if this button is currently pressed according to
	 *         {@code buttons}, {@code false} otherwise.
	 */
	public boolean isPressed(XInputButtons buttons) {
		try {
			return field.getBoolean(buttons);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
