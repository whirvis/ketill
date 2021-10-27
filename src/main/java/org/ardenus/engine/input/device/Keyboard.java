package org.ardenus.engine.input.device;

import org.ardenus.engine.input.device.adapter.DeviceAdapter;
import org.ardenus.engine.input.device.feature.Button1bc;
import org.ardenus.engine.input.device.feature.FeaturePresent;
import org.ardenus.engine.input.device.feature.KeyboardKey;
import org.ardenus.engine.input.device.feature.monitor.DeviceButtonMonitor;

/**
 * A keyboard which can send and receive input data.
 * <p>
 * <b>Note:</b> For a keyboard to work properly, it must be polled via
 * {@link #poll()} before querying any input information. It is recommended to
 * poll the keyboard once on every application update.
 * 
 * @see Mouse
 * @see Controller
 */
@DeviceId("keyboard")
public class Keyboard extends InputDevice {

	/* @formatter: off */
	@FeaturePresent /* printable keys */
	public static final KeyboardKey
			SPACE = new KeyboardKey("space"),
			APOSTROPHE = new KeyboardKey("apostrophe"),
			COMMA = new KeyboardKey("comma"),
			MINUS = new KeyboardKey("minus"),
			PERIOD = new KeyboardKey("period"),
			SLASH = new KeyboardKey("slash"),
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
			SEMICOLON = new KeyboardKey("semicolon"),
			EQUAL = new KeyboardKey("equal"),
			A = new KeyboardKey("a"),
			B = new KeyboardKey("b"),
			C = new KeyboardKey("c"),
			D = new KeyboardKey("d"),
			E = new KeyboardKey("e"),
			F = new KeyboardKey("f"),
			G = new KeyboardKey("g"),
			H = new KeyboardKey("h"),
			I = new KeyboardKey("i"),
			J = new KeyboardKey("j"),
			K = new KeyboardKey("k"),
			L = new KeyboardKey("l"),
			M = new KeyboardKey("m"),
			N = new KeyboardKey("n"),
			O = new KeyboardKey("o"),
			P = new KeyboardKey("p"),
			Q = new KeyboardKey("q"),
			R = new KeyboardKey("r"),
			S = new KeyboardKey("s"),
			T = new KeyboardKey("t"),
			U = new KeyboardKey("u"),
			V = new KeyboardKey("v"),
			W = new KeyboardKey("w"),
			X = new KeyboardKey("x"),
			Y = new KeyboardKey("y"),
			Z = new KeyboardKey("z"),
			LEFT_BRACKET = new KeyboardKey("left_bracket"),
			BACKSLASH = new KeyboardKey("backslash"),
			RIGHT_BRACKET = new KeyboardKey("right_bracket"),
			GRAVE_ACCENT = new KeyboardKey("grave_accent"),
			WORLD_1 = new KeyboardKey("world_1"),
			WORLD_2 = new KeyboardKey("world_2");

	@FeaturePresent /* method keys */
	public static final KeyboardKey
			ESCAPE = new KeyboardKey("escape"),
			ENTER = new KeyboardKey("enter"),
			TAB = new KeyboardKey("tab"),
			BACKSPACE = new KeyboardKey("backspace"),
			INSERT = new KeyboardKey("insert"),
			DELETE = new KeyboardKey("delete"),
			RIGHT = new KeyboardKey("right"),
			LEFT = new KeyboardKey("left"),
			DOWN = new KeyboardKey("down"),
			UP = new KeyboardKey("up"),
			PAGE_UP = new KeyboardKey("page_up"),
			PAGE_DOWN = new KeyboardKey("page_down"),
			HOME = new KeyboardKey("home"),
			END = new KeyboardKey("end"),
			CAPS_LOCK = new KeyboardKey("caps_lock"),
			SCROLL_LOCK = new KeyboardKey("scroll_lock"),
			NUM_LOCK = new KeyboardKey("num_lock"),
			PRINT_SCREEN = new KeyboardKey("print_screen"),
			PAUSE = new KeyboardKey("pause"),
			F1 = new KeyboardKey("f1"),
			F2 = new KeyboardKey("f2"),
			F3 = new KeyboardKey("f3"),
			F4 = new KeyboardKey("f4"),
			F5 = new KeyboardKey("f5"),
			F6 = new KeyboardKey("f6"),
			F7 = new KeyboardKey("f7"),
			F8 = new KeyboardKey("f8"),
			F9 = new KeyboardKey("f9"),
			F10 = new KeyboardKey("f10"),
			F11 = new KeyboardKey("f11"),
			F12 = new KeyboardKey("f12"),
			F13 = new KeyboardKey("f13"),
			F14 = new KeyboardKey("f14"),
			F15 = new KeyboardKey("f15"),
			F16 = new KeyboardKey("f16"),
			F17 = new KeyboardKey("f17"),
			F18 = new KeyboardKey("f18"),
			F19 = new KeyboardKey("f19"),
			F20 = new KeyboardKey("f20"),
			F21 = new KeyboardKey("f21"),
			F22 = new KeyboardKey("f22"),
			F23 = new KeyboardKey("f23"),
			F24 = new KeyboardKey("f24"),
			F25 = new KeyboardKey("f25"),
			KP_0 = new KeyboardKey("kp_0"),
			KP_1 = new KeyboardKey("kp_1"),
			KP_2 = new KeyboardKey("kp_2"),
			KP_3 = new KeyboardKey("kp_3"),
			KP_4 = new KeyboardKey("kp_4"),
			KP_5 = new KeyboardKey("kp_5"),
			KP_6 = new KeyboardKey("kp_6"),
			KP_7 = new KeyboardKey("kp_7"),
			KP_8 = new KeyboardKey("kp_8"),
			KP_9 = new KeyboardKey("kp_9"),
			KP_DOT = new KeyboardKey("kp_dot"),
			KP_DIV = new KeyboardKey("kp_div"),
			KP_MUL = new KeyboardKey("kp_mul"),
			KP_SUB = new KeyboardKey("kp_sub"),
			KP_ADD = new KeyboardKey("kp_add"),
			KP_ENTER = new KeyboardKey("kp_enter"),
			KP_EQUAL = new KeyboardKey("kp_equal"),
			LEFT_SHIFT = new KeyboardKey("left_shift"),
			LEFT_CONTROL = new KeyboardKey("left_ctrl"),
			LEFT_ALT = new KeyboardKey("left_alt"),
			LEFT_SUPER = new KeyboardKey("left_super"),
			RIGHT_SHIFT = new KeyboardKey("right_shift"),
			RIGHT_CONTROL = new KeyboardKey("right_ctrl"),
			RIGHT_ALT = new KeyboardKey("right_alt"),
			RIGHT_SUPER = new KeyboardKey("right_super"),
			MENU = new KeyboardKey("menu");
	/* @formatter: on */

	/**
	 * By default, a {@code Keyboard} expects device features of type
	 * {@link KeyboardKey}. As a result, an instance of
	 * {@link DeviceButtonMonitor} will be added on instantiation.
	 * 
	 * @param adapter
	 *            the device adapter.
	 * @throws NullPointerException
	 *             if {@code adapter} is {@code null}.
	 */
	public Keyboard(DeviceAdapter<Keyboard> adapter) {
		super(adapter);
		this.addMonitor(new DeviceButtonMonitor(this));
	}

	/**
	 * @param key
	 *            the keyboard key.
	 * @return {@code true} if {@code key} is pressed, {@code false} otherwise.
	 */
	public boolean isPressed(KeyboardKey key) {
		Button1bc state = this.getState(key);
		return state.pressed();
	}
	
}
