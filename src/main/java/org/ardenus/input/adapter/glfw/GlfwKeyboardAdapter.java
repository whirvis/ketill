package org.ardenus.input.adapter.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.ardenus.input.Keyboard;
import org.ardenus.input.adapter.AdapterMapping;
import org.ardenus.input.adapter.FeatureAdapter;
import org.ardenus.input.feature.Button1b;

public class GlfwKeyboardAdapter extends GlfwDeviceAdapter<Keyboard> {

	/* @formatter: off */
	@AdapterMapping
	public static final GlfwKeyMapping
			SPACE = new GlfwKeyMapping(Keyboard.SPACE, GLFW_KEY_SPACE),
			APOSTROPHE = new GlfwKeyMapping(Keyboard.APOSTROPHE, GLFW_KEY_APOSTROPHE),
			COMMA = new GlfwKeyMapping(Keyboard.COMMA, GLFW_KEY_COMMA),
			MINUS = new GlfwKeyMapping(Keyboard.MINUS, GLFW_KEY_MINUS),
			PERIOD = new GlfwKeyMapping(Keyboard.PERIOD, GLFW_KEY_PERIOD),
			SLASH = new GlfwKeyMapping(Keyboard.SLASH, GLFW_KEY_SLASH),
			ZERO = new GlfwKeyMapping(Keyboard.ZERO, GLFW_KEY_0),
			ONE = new GlfwKeyMapping(Keyboard.ONE, GLFW_KEY_1),
			TWO = new GlfwKeyMapping(Keyboard.TWO, GLFW_KEY_2),
			THREE = new GlfwKeyMapping(Keyboard.THREE, GLFW_KEY_3),
			FOUR = new GlfwKeyMapping(Keyboard.FOUR, GLFW_KEY_4),
			FIVE = new GlfwKeyMapping(Keyboard.FIVE, GLFW_KEY_5),
			SIX = new GlfwKeyMapping(Keyboard.SIX, GLFW_KEY_6),
			SEVEN = new GlfwKeyMapping(Keyboard.SEVEN, GLFW_KEY_7),
			EIGHT = new GlfwKeyMapping(Keyboard.EIGHT, GLFW_KEY_8),
			NINE = new GlfwKeyMapping(Keyboard.NINE, GLFW_KEY_9),
			SEMICOLON = new GlfwKeyMapping(Keyboard.SEMICOLON, GLFW_KEY_SEMICOLON),
			EQUAL = new GlfwKeyMapping(Keyboard.EQUAL, GLFW_KEY_EQUAL),
			A = new GlfwKeyMapping(Keyboard.A, GLFW_KEY_A),
			B = new GlfwKeyMapping(Keyboard.B, GLFW_KEY_B),
			C = new GlfwKeyMapping(Keyboard.C, GLFW_KEY_C),
			D = new GlfwKeyMapping(Keyboard.D, GLFW_KEY_D),
			E = new GlfwKeyMapping(Keyboard.E, GLFW_KEY_E),
			F = new GlfwKeyMapping(Keyboard.F, GLFW_KEY_F),
			G = new GlfwKeyMapping(Keyboard.G, GLFW_KEY_G),
			H = new GlfwKeyMapping(Keyboard.H, GLFW_KEY_H),
			I = new GlfwKeyMapping(Keyboard.I, GLFW_KEY_I),
			J = new GlfwKeyMapping(Keyboard.J, GLFW_KEY_J),
			K = new GlfwKeyMapping(Keyboard.K, GLFW_KEY_K),
			L = new GlfwKeyMapping(Keyboard.L, GLFW_KEY_L),
			M = new GlfwKeyMapping(Keyboard.M, GLFW_KEY_M),
			N = new GlfwKeyMapping(Keyboard.N, GLFW_KEY_N),
			O = new GlfwKeyMapping(Keyboard.O, GLFW_KEY_O),
			P = new GlfwKeyMapping(Keyboard.P, GLFW_KEY_P),
			Q = new GlfwKeyMapping(Keyboard.Q, GLFW_KEY_Q),
			R = new GlfwKeyMapping(Keyboard.R, GLFW_KEY_R),
			S = new GlfwKeyMapping(Keyboard.S, GLFW_KEY_S),
			T = new GlfwKeyMapping(Keyboard.T, GLFW_KEY_T),
			U = new GlfwKeyMapping(Keyboard.U, GLFW_KEY_U),
			V = new GlfwKeyMapping(Keyboard.V, GLFW_KEY_V),
			W = new GlfwKeyMapping(Keyboard.W, GLFW_KEY_W),
			X = new GlfwKeyMapping(Keyboard.X, GLFW_KEY_X),
			Y = new GlfwKeyMapping(Keyboard.Y, GLFW_KEY_Y),
			Z = new GlfwKeyMapping(Keyboard.Z, GLFW_KEY_Z),
			LEFT_BRACKET = new GlfwKeyMapping(Keyboard.LEFT_BRACKET, GLFW_KEY_LEFT_BRACKET),
			BACKSLASH = new GlfwKeyMapping(Keyboard.BACKSLASH, GLFW_KEY_BACKSLASH),
			RIGHT_BRACKET = new GlfwKeyMapping(Keyboard.RIGHT_BRACKET, GLFW_KEY_RIGHT_BRACKET),
			GRAVE_ACCENT = new GlfwKeyMapping(Keyboard.GRAVE_ACCENT, GLFW_KEY_GRAVE_ACCENT),
			WORLD_1 = new GlfwKeyMapping(Keyboard.WORLD_1, GLFW_KEY_WORLD_1),
			WORLD_2 = new GlfwKeyMapping(Keyboard.WORLD_2, GLFW_KEY_WORLD_2);

	@AdapterMapping
	public static final GlfwKeyMapping
			ESCAPE = new GlfwKeyMapping(Keyboard.ESCAPE, GLFW_KEY_ESCAPE),
			ENTER = new GlfwKeyMapping(Keyboard.ENTER, GLFW_KEY_ENTER),
			TAB = new GlfwKeyMapping(Keyboard.TAB, GLFW_KEY_TAB),
			BACKSPACE = new GlfwKeyMapping(Keyboard.BACKSPACE, GLFW_KEY_BACKSPACE),
			INSERT = new GlfwKeyMapping(Keyboard.INSERT, GLFW_KEY_INSERT),
			DELETE = new GlfwKeyMapping(Keyboard.DELETE, GLFW_KEY_DELETE),
			RIGHT = new GlfwKeyMapping(Keyboard.RIGHT, GLFW_KEY_RIGHT),
			LEFT = new GlfwKeyMapping(Keyboard.LEFT, GLFW_KEY_LEFT),
			DOWN = new GlfwKeyMapping(Keyboard.DOWN, GLFW_KEY_DOWN),
			UP = new GlfwKeyMapping(Keyboard.UP, GLFW_KEY_UP),
			PAGE_UP = new GlfwKeyMapping(Keyboard.PAGE_UP, GLFW_KEY_PAGE_UP),
			PAGE_DOWN = new GlfwKeyMapping(Keyboard.PAGE_DOWN, GLFW_KEY_PAGE_DOWN),
			HOME = new GlfwKeyMapping(Keyboard.HOME, GLFW_KEY_HOME),
			END = new GlfwKeyMapping(Keyboard.END, GLFW_KEY_END),
			CAPS_LOCK = new GlfwKeyMapping(Keyboard.CAPS_LOCK, GLFW_KEY_CAPS_LOCK),
			SCROLL_LOCK = new GlfwKeyMapping(Keyboard.SCROLL_LOCK, GLFW_KEY_SCROLL_LOCK),
			NUM_LOCK = new GlfwKeyMapping(Keyboard.NUM_LOCK, GLFW_KEY_NUM_LOCK),
			PRINT_SCREEN = new GlfwKeyMapping(Keyboard.PRINT_SCREEN, GLFW_KEY_PRINT_SCREEN),
			PAUSE = new GlfwKeyMapping(Keyboard.PAUSE, GLFW_KEY_PAUSE),
			F1 = new GlfwKeyMapping(Keyboard.F1, GLFW_KEY_F1),
			F2 = new GlfwKeyMapping(Keyboard.F2, GLFW_KEY_F2),
			F3 = new GlfwKeyMapping(Keyboard.F3, GLFW_KEY_F3),
			F4 = new GlfwKeyMapping(Keyboard.F4, GLFW_KEY_F4),
			F5 = new GlfwKeyMapping(Keyboard.F5, GLFW_KEY_F5),
			F6 = new GlfwKeyMapping(Keyboard.F6, GLFW_KEY_F6),
			F7 = new GlfwKeyMapping(Keyboard.F7, GLFW_KEY_F7),
			F8 = new GlfwKeyMapping(Keyboard.F8, GLFW_KEY_F8),
			F9 = new GlfwKeyMapping(Keyboard.F9, GLFW_KEY_F9),
			F10 = new GlfwKeyMapping(Keyboard.F10, GLFW_KEY_F10),
			F11 = new GlfwKeyMapping(Keyboard.F11, GLFW_KEY_F11),
			F12 = new GlfwKeyMapping(Keyboard.F12, GLFW_KEY_F12),
			F13 = new GlfwKeyMapping(Keyboard.F13, GLFW_KEY_F13),
			F14 = new GlfwKeyMapping(Keyboard.F14, GLFW_KEY_F14),
			F15 = new GlfwKeyMapping(Keyboard.F15, GLFW_KEY_F15),
			F16 = new GlfwKeyMapping(Keyboard.F16, GLFW_KEY_F16),
			F17 = new GlfwKeyMapping(Keyboard.F17, GLFW_KEY_F17),
			F18 = new GlfwKeyMapping(Keyboard.F18, GLFW_KEY_F18),
			F19 = new GlfwKeyMapping(Keyboard.F19, GLFW_KEY_F19),
			F20 = new GlfwKeyMapping(Keyboard.F20, GLFW_KEY_F20),
			F21 = new GlfwKeyMapping(Keyboard.F21, GLFW_KEY_F21),
			F22 = new GlfwKeyMapping(Keyboard.F22, GLFW_KEY_F22),
			F23 = new GlfwKeyMapping(Keyboard.F23, GLFW_KEY_F23),
			F24 = new GlfwKeyMapping(Keyboard.F24, GLFW_KEY_F24),
			F25 = new GlfwKeyMapping(Keyboard.F25, GLFW_KEY_F25),
			KP_0 = new GlfwKeyMapping(Keyboard.KP_0, GLFW_KEY_KP_0),
			KP_1 = new GlfwKeyMapping(Keyboard.KP_1, GLFW_KEY_KP_1),
			KP_2 = new GlfwKeyMapping(Keyboard.KP_2, GLFW_KEY_KP_2),
			KP_3 = new GlfwKeyMapping(Keyboard.KP_3, GLFW_KEY_KP_3),
			KP_4 = new GlfwKeyMapping(Keyboard.KP_4, GLFW_KEY_KP_4),
			KP_5 = new GlfwKeyMapping(Keyboard.KP_5, GLFW_KEY_KP_5),
			KP_6 = new GlfwKeyMapping(Keyboard.KP_6, GLFW_KEY_KP_6),
			KP_7 = new GlfwKeyMapping(Keyboard.KP_7, GLFW_KEY_KP_7),
			KP_8 = new GlfwKeyMapping(Keyboard.KP_8, GLFW_KEY_KP_8),
			KP_9 = new GlfwKeyMapping(Keyboard.KP_9, GLFW_KEY_KP_9),
			KP_DOT = new GlfwKeyMapping(Keyboard.KP_DOT, GLFW_KEY_KP_DECIMAL),
			KP_DIV = new GlfwKeyMapping(Keyboard.KP_DIV, GLFW_KEY_KP_DIVIDE),
			KP_MUL = new GlfwKeyMapping(Keyboard.KP_MUL, GLFW_KEY_KP_MULTIPLY),
			KP_SUB = new GlfwKeyMapping(Keyboard.KP_SUB, GLFW_KEY_KP_SUBTRACT),
			KP_ADD = new GlfwKeyMapping(Keyboard.KP_ADD, GLFW_KEY_KP_ADD),
			KP_ENTER = new GlfwKeyMapping(Keyboard.KP_ENTER, GLFW_KEY_KP_ENTER),
			KP_EQUAL = new GlfwKeyMapping(Keyboard.KP_EQUAL, GLFW_KEY_KP_EQUAL),
			LEFT_SHIFT = new GlfwKeyMapping(Keyboard.LEFT_SHIFT, GLFW_KEY_LEFT_SHIFT),
			LEFT_CONTROL = new GlfwKeyMapping(Keyboard.LEFT_CONTROL, GLFW_KEY_LEFT_CONTROL),
			LEFT_ALT = new GlfwKeyMapping(Keyboard.LEFT_ALT, GLFW_KEY_LEFT_ALT),
			LEFT_SUPER = new GlfwKeyMapping(Keyboard.LEFT_SUPER, GLFW_KEY_LEFT_SUPER),
			RIGHT_SHIFT = new GlfwKeyMapping(Keyboard.RIGHT_SHIFT, GLFW_KEY_RIGHT_SHIFT),
			RIGHT_CONTROL = new GlfwKeyMapping(Keyboard.RIGHT_CONTROL, GLFW_KEY_RIGHT_CONTROL),
			RIGHT_ALT = new GlfwKeyMapping(Keyboard.RIGHT_ALT, GLFW_KEY_RIGHT_ALT),
			RIGHT_SUPER = new GlfwKeyMapping(Keyboard.RIGHT_SUPER, GLFW_KEY_RIGHT_SUPER),
			MENU = new GlfwKeyMapping(Keyboard.MENU, GLFW_KEY_MENU);
	/* @formatter: on */

	public GlfwKeyboardAdapter(long ptr_glfwWindow) {
		super(ptr_glfwWindow);
	}

	@Override
	public boolean isConnected() {
		return true; /* keyboard is always connected */
	}

	@FeatureAdapter
	public void isPressed(GlfwKeyMapping mapping, Button1b button) {
		int status = glfwGetKey(ptr_glfwWindow, mapping.glfwKey);
		button.pressed = status >= GLFW_PRESS;
	}

	@Override
	public void poll() {
		/* nothing to poll */
	}

}
