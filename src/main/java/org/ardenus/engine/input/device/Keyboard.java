package org.ardenus.engine.input.device;

import java.util.HashMap;
import java.util.Map;

import org.ardenus.engine.input.Input;
import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.event.FeaturePressEvent;
import org.ardenus.engine.input.device.event.FeatureReleaseEvent;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.DeviceFeature;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.ardenus.engine.input.device.feature.KeyboardKey;

/**
 * A generic keyboard.
 * 
 * @see InputDevice
 */
public class Keyboard extends InputDevice {

	/* @formatter: off */
	@FeaturePresent /* printable keys */
	public static final KeyboardKey
			SPACE = new KeyboardKey("Space"),
			APOSTROPHE = new KeyboardKey("\'"),
			COMMA = new KeyboardKey(","),
			MINUS = new KeyboardKey("-"),
			PERIOD = new KeyboardKey("."),
			SLASH = new KeyboardKey("/"),
			ZERO = new KeyboardKey("0"),
			ONE = new KeyboardKey("1"),
			TWO = new KeyboardKey("2"),
			THREE = new KeyboardKey("3"),
			FOUR = new KeyboardKey("4"),
			FIVE = new KeyboardKey("5"),
			SIX = new KeyboardKey("6"),
			SEVEN = new KeyboardKey("7"),
			EIGHT = new KeyboardKey("8"),
			NINE = new KeyboardKey("9"),
			SEMICOLON = new KeyboardKey(";"),
			EQUAL = new KeyboardKey("="),
			A = new KeyboardKey("A"),
			B = new KeyboardKey("B"),
			C = new KeyboardKey("C"),
			D = new KeyboardKey("D"),
			E = new KeyboardKey("E"),
			F = new KeyboardKey("F"),
			G = new KeyboardKey("G"),
			H = new KeyboardKey("H"),
			I = new KeyboardKey("I"),
			J = new KeyboardKey("J"),
			K = new KeyboardKey("K"),
			L = new KeyboardKey("L"),
			M = new KeyboardKey("M"),
			N = new KeyboardKey("N"),
			O = new KeyboardKey("O"),
			P = new KeyboardKey("P"),
			Q = new KeyboardKey("Q"),
			R = new KeyboardKey("R"),
			S = new KeyboardKey("S"),
			T = new KeyboardKey("T"),
			U = new KeyboardKey("U"),
			V = new KeyboardKey("V"),
			W = new KeyboardKey("W"),
			X = new KeyboardKey("X"),
			Y = new KeyboardKey("Y"),
			Z = new KeyboardKey("Z"),
			LEFT_BRACKET = new KeyboardKey("["),
			BACKSLASH = new KeyboardKey("\\"),
			RIGHT_BRACKET = new KeyboardKey("]"),
			GRAVE_ACCENT = new KeyboardKey("`"),
			WORLD_1 = new KeyboardKey("World 1"),
			WORLD_2 = new KeyboardKey("World 2");

	@FeaturePresent /* function keys */
	public static final KeyboardKey
			ESCAPE = new KeyboardKey("Escape"),
			ENTER = new KeyboardKey("Enter"),
			TAB = new KeyboardKey("Tab"),
			BACKSPACE = new KeyboardKey("Backspace"),
			INSERT = new KeyboardKey("Insert"),
			DELETE = new KeyboardKey("Delete"),
			RIGHT = new KeyboardKey("Right"),
			LEFT = new KeyboardKey("Left"),
			DOWN = new KeyboardKey("Down"),
			UP = new KeyboardKey("Up"),
			PAGE_UP = new KeyboardKey("Page Up"),
			PAGE_DOWN = new KeyboardKey("Page Down"),
			HOME = new KeyboardKey("Home"),
			END = new KeyboardKey("End"),
			CAPS_LOCK = new KeyboardKey("Caps Lock"),
			SCROLL_LOCK = new KeyboardKey("Scroll Lock"),
			NUM_LOCK = new KeyboardKey("Num Lock"),
			PRINT_SCREEN = new KeyboardKey("Print Screen"),
			PAUSE = new KeyboardKey("Pause"),
			F1 = new KeyboardKey("F1"),
			F2 = new KeyboardKey("F2"),
			F3 = new KeyboardKey("F3"),
			F4 = new KeyboardKey("F4"),
			F5 = new KeyboardKey("F5"),
			F6 = new KeyboardKey("F6"),
			F7 = new KeyboardKey("F7"),
			F8 = new KeyboardKey("F8"),
			F9 = new KeyboardKey("F9"),
			F10 = new KeyboardKey("F10"),
			F11 = new KeyboardKey("F11"),
			F12 = new KeyboardKey("F12"),
			F13 = new KeyboardKey("F13"),
			F14 = new KeyboardKey("F14"),
			F15 = new KeyboardKey("F15"),
			F16 = new KeyboardKey("F16"),
			F17 = new KeyboardKey("F17"),
			F18 = new KeyboardKey("F18"),
			F19 = new KeyboardKey("F19"),
			F20 = new KeyboardKey("F20"),
			F21 = new KeyboardKey("F21"),
			F22 = new KeyboardKey("F22"),
			F23 = new KeyboardKey("F23"),
			F24 = new KeyboardKey("F24"),
			F25 = new KeyboardKey("F25"),
			KP_0 = new KeyboardKey("Keypad 0"),
			KP_1 = new KeyboardKey("Keypad 1"),
			KP_2 = new KeyboardKey("Keypad 2"),
			KP_3 = new KeyboardKey("Keypad 3"),
			KP_4 = new KeyboardKey("Keypad 4"),
			KP_5 = new KeyboardKey("Keypad 5"),
			KP_6 = new KeyboardKey("Keypad 6"),
			KP_7 = new KeyboardKey("Keypad 7"),
			KP_8 = new KeyboardKey("Keypad 8"),
			KP_9 = new KeyboardKey("Keypad 9"),
			KP_DECIMAL = new KeyboardKey("Keypad ."),
			KP_DIVIDE = new KeyboardKey("Keypad /"),
			KP_MULTIPLY = new KeyboardKey("Keypad *"),
			KP_SUBTRACT = new KeyboardKey("Keypad -"),
			KP_ADD = new KeyboardKey("Keypad +"),
			KP_ENTER = new KeyboardKey("Keypad Enter"),
			KP_EQUAL = new KeyboardKey("Keypad ="),
			LEFT_SHIFT = new KeyboardKey("Left Shift"),
			LEFT_CONTROL = new KeyboardKey("Left CTRL"),
			LEFT_ALT = new KeyboardKey("Left Alt"),
			LEFT_SUPER = new KeyboardKey("Left Super"),
			RIGHT_SHIFT = new KeyboardKey("Right Shift"),
			RIGHT_CONTROL = new KeyboardKey("Right CTRL"),
			RIGHT_ALT = new KeyboardKey("Right Alt"),
			RIGHT_SUPER = new KeyboardKey("Right Super"),
			MENU = new KeyboardKey("Menu");
	/* @formatter: on */

	private static class PressableState {

		public boolean pressed;
		public long pressTime;
		public boolean held;
		public long lastHeldPress;

	}

	private final Map<KeyboardKey, PressableState> keyStates;

	/**
	 * Constructs a new {@code Keyboard}.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Keyboard(DeviceAdapter<Keyboard> adapter) {
		super(adapter);
		this.keyStates = new HashMap<>();
	}

	public boolean isPressed(KeyboardKey key) {
		Button1bc state = this.getState(key);
		return state.pressed();
	}

	private void pollKey(long time, KeyboardKey key) {
		PressableState state = keyStates.get(key);
		if (state == null) {
			state = new PressableState();
			keyStates.put(key, state);
		}

		boolean pressed = this.isPressed(key);

		boolean wasPressed = state.pressed;

		if (!wasPressed && pressed) {
			Input.sendEvent(new FeaturePressEvent(this, key, null, false));
			state.pressTime = time;
		} else if (wasPressed && !pressed) {
			Input.sendEvent(
					new FeatureReleaseEvent(this, key, null, state.held));
			state.held = false;
		}

		state.pressed = pressed;
		if (state.pressed) {
			if (!state.held && time - state.pressTime >= 1000L) {
				/* TODO: initial hold event */
				state.held = true;
			}

			if (state.held && time - state.lastHeldPress >= 100L) {
				Input.sendEvent(new FeaturePressEvent(this, key, null, true));
				state.lastHeldPress = time;
			}
		}
	}

	@Override
	public void poll() {
		long time = System.currentTimeMillis();
		super.poll();
		for (DeviceFeature<?> feature : this.getFeatures()) {
			if (feature instanceof KeyboardKey) {
				this.pollKey(time, (KeyboardKey) feature);
			}
		}
	}

}
