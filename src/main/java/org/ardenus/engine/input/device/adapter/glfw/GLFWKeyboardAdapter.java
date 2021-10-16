package org.ardenus.engine.input.device.adapter.glfw;

import static org.lwjgl.glfw.GLFW.*;

import org.ardenus.engine.input.device.Keyboard;
import org.ardenus.engine.input.device.adapter.AdapterMapping;
import org.ardenus.engine.input.device.adapter.FeatureAdapter;
import org.ardenus.engine.input.device.adapter.KeyMapping;
import org.ardenus.engine.input.device.feature.Button1b;

public class GLFWKeyboardAdapter extends GLFWDeviceAdapter<Keyboard> {

	/* @formatter: off */
	@AdapterMapping
	public static final KeyMapping
			SPACE = new GLFWKeyMapping(Keyboard.SPACE, GLFW_KEY_SPACE),
			APOSTROPHE = new GLFWKeyMapping(Keyboard.APOSTROPHE, GLFW_KEY_APOSTROPHE),
			COMMA = new GLFWKeyMapping(Keyboard.COMMA, GLFW_KEY_COMMA),
			MINUS = new GLFWKeyMapping(Keyboard.MINUS, GLFW_KEY_MINUS),
			PERIOD = new GLFWKeyMapping(Keyboard.PERIOD, GLFW_KEY_PERIOD),
			SLASH = new GLFWKeyMapping(Keyboard.SLASH, GLFW_KEY_SLASH),
			ZERO = new GLFWKeyMapping(Keyboard.ZERO, GLFW_KEY_0),
			ONE = new GLFWKeyMapping(Keyboard.ONE, GLFW_KEY_1),
			TWO = new GLFWKeyMapping(Keyboard.TWO, GLFW_KEY_2),
			THREE = new GLFWKeyMapping(Keyboard.THREE, GLFW_KEY_3),
			FOUR = new GLFWKeyMapping(Keyboard.FOUR, GLFW_KEY_4),
			FIVE = new GLFWKeyMapping(Keyboard.FIVE, GLFW_KEY_5),
			SIX = new GLFWKeyMapping(Keyboard.SIX, GLFW_KEY_6),
			SEVEN = new GLFWKeyMapping(Keyboard.SEVEN, GLFW_KEY_7),
			EIGHT = new GLFWKeyMapping(Keyboard.EIGHT, GLFW_KEY_8),
			NINE = new GLFWKeyMapping(Keyboard.NINE, GLFW_KEY_9),
			SEMICOLON = new GLFWKeyMapping(Keyboard.SEMICOLON, GLFW_KEY_SEMICOLON),
			EQUAL = new GLFWKeyMapping(Keyboard.EQUAL, GLFW_KEY_EQUAL),
			A = new GLFWKeyMapping(Keyboard.A, GLFW_KEY_A),
			B = new GLFWKeyMapping(Keyboard.B, GLFW_KEY_B),
			C = new GLFWKeyMapping(Keyboard.C, GLFW_KEY_C),
			D = new GLFWKeyMapping(Keyboard.D, GLFW_KEY_D),
			E = new GLFWKeyMapping(Keyboard.E, GLFW_KEY_E),
			F = new GLFWKeyMapping(Keyboard.F, GLFW_KEY_F),
			G = new GLFWKeyMapping(Keyboard.G, GLFW_KEY_G),
			H = new GLFWKeyMapping(Keyboard.H, GLFW_KEY_H),
			I = new GLFWKeyMapping(Keyboard.I, GLFW_KEY_I),
			J = new GLFWKeyMapping(Keyboard.J, GLFW_KEY_J),
			K = new GLFWKeyMapping(Keyboard.K, GLFW_KEY_K),
			L = new GLFWKeyMapping(Keyboard.L, GLFW_KEY_L),
			M = new GLFWKeyMapping(Keyboard.M, GLFW_KEY_M),
			N = new GLFWKeyMapping(Keyboard.N, GLFW_KEY_N),
			O = new GLFWKeyMapping(Keyboard.O, GLFW_KEY_O),
			P = new GLFWKeyMapping(Keyboard.P, GLFW_KEY_P),
			Q = new GLFWKeyMapping(Keyboard.Q, GLFW_KEY_Q),
			R = new GLFWKeyMapping(Keyboard.R, GLFW_KEY_R),
			S = new GLFWKeyMapping(Keyboard.S, GLFW_KEY_S),
			T = new GLFWKeyMapping(Keyboard.T, GLFW_KEY_T),
			U = new GLFWKeyMapping(Keyboard.U, GLFW_KEY_U),
			V = new GLFWKeyMapping(Keyboard.V, GLFW_KEY_V),
			W = new GLFWKeyMapping(Keyboard.W, GLFW_KEY_W),
			X = new GLFWKeyMapping(Keyboard.X, GLFW_KEY_X),
			Y = new GLFWKeyMapping(Keyboard.Y, GLFW_KEY_Y),
			Z = new GLFWKeyMapping(Keyboard.Z, GLFW_KEY_Z),
			LEFT_BRACKET = new GLFWKeyMapping(Keyboard.LEFT_BRACKET, GLFW_KEY_LEFT_BRACKET),
			BACKSLASH = new GLFWKeyMapping(Keyboard.BACKSLASH, GLFW_KEY_BACKSLASH),
			RIGHT_BRACKET = new GLFWKeyMapping(Keyboard.RIGHT_BRACKET, GLFW_KEY_RIGHT_BRACKET),
			GRAVE_ACCENT = new GLFWKeyMapping(Keyboard.GRAVE_ACCENT, GLFW_KEY_GRAVE_ACCENT),
			WORLD_1 = new GLFWKeyMapping(Keyboard.WORLD_1, GLFW_KEY_WORLD_1),
			WORLD_2 = new GLFWKeyMapping(Keyboard.WORLD_2, GLFW_KEY_WORLD_2);

	@AdapterMapping
	public static final KeyMapping
			ESCAPE = new GLFWKeyMapping(Keyboard.ESCAPE, GLFW_KEY_ESCAPE),
			ENTER = new GLFWKeyMapping(Keyboard.ENTER, GLFW_KEY_ENTER),
			TAB = new GLFWKeyMapping(Keyboard.TAB, GLFW_KEY_TAB),
			BACKSPACE = new GLFWKeyMapping(Keyboard.BACKSPACE, GLFW_KEY_BACKSPACE),
			INSERT = new GLFWKeyMapping(Keyboard.INSERT, GLFW_KEY_INSERT),
			DELETE = new GLFWKeyMapping(Keyboard.DELETE, GLFW_KEY_DELETE),
			RIGHT = new GLFWKeyMapping(Keyboard.RIGHT, GLFW_KEY_RIGHT),
			LEFT = new GLFWKeyMapping(Keyboard.LEFT, GLFW_KEY_LEFT),
			DOWN = new GLFWKeyMapping(Keyboard.DOWN, GLFW_KEY_DOWN),
			UP = new GLFWKeyMapping(Keyboard.UP, GLFW_KEY_UP),
			PAGE_UP = new GLFWKeyMapping(Keyboard.PAGE_UP, GLFW_KEY_PAGE_UP),
			PAGE_DOWN = new GLFWKeyMapping(Keyboard.PAGE_DOWN, GLFW_KEY_PAGE_DOWN),
			HOME = new GLFWKeyMapping(Keyboard.HOME, GLFW_KEY_HOME),
			END = new GLFWKeyMapping(Keyboard.END, GLFW_KEY_END),
			CAPS_LOCK = new GLFWKeyMapping(Keyboard.CAPS_LOCK, GLFW_KEY_CAPS_LOCK),
			SCROLL_LOCK = new GLFWKeyMapping(Keyboard.SCROLL_LOCK, GLFW_KEY_SCROLL_LOCK),
			NUM_LOCK = new GLFWKeyMapping(Keyboard.NUM_LOCK, GLFW_KEY_NUM_LOCK),
			PRINT_SCREEN = new GLFWKeyMapping(Keyboard.PRINT_SCREEN, GLFW_KEY_PRINT_SCREEN),
			PAUSE = new GLFWKeyMapping(Keyboard.PAUSE, GLFW_KEY_PAUSE),
			F1 = new GLFWKeyMapping(Keyboard.F1, GLFW_KEY_F1),
			F2 = new GLFWKeyMapping(Keyboard.F2, GLFW_KEY_F2),
			F3 = new GLFWKeyMapping(Keyboard.F3, GLFW_KEY_F3),
			F4 = new GLFWKeyMapping(Keyboard.F4, GLFW_KEY_F4),
			F5 = new GLFWKeyMapping(Keyboard.F5, GLFW_KEY_F5),
			F6 = new GLFWKeyMapping(Keyboard.F6, GLFW_KEY_F6),
			F7 = new GLFWKeyMapping(Keyboard.F7, GLFW_KEY_F7),
			F8 = new GLFWKeyMapping(Keyboard.F8, GLFW_KEY_F8),
			F9 = new GLFWKeyMapping(Keyboard.F9, GLFW_KEY_F9),
			F10 = new GLFWKeyMapping(Keyboard.F10, GLFW_KEY_F10),
			F11 = new GLFWKeyMapping(Keyboard.F11, GLFW_KEY_F11),
			F12 = new GLFWKeyMapping(Keyboard.F12, GLFW_KEY_F12),
			F13 = new GLFWKeyMapping(Keyboard.F13, GLFW_KEY_F13),
			F14 = new GLFWKeyMapping(Keyboard.F14, GLFW_KEY_F14),
			F15 = new GLFWKeyMapping(Keyboard.F15, GLFW_KEY_F15),
			F16 = new GLFWKeyMapping(Keyboard.F16, GLFW_KEY_F16),
			F17 = new GLFWKeyMapping(Keyboard.F17, GLFW_KEY_F17),
			F18 = new GLFWKeyMapping(Keyboard.F18, GLFW_KEY_F18),
			F19 = new GLFWKeyMapping(Keyboard.F19, GLFW_KEY_F19),
			F20 = new GLFWKeyMapping(Keyboard.F20, GLFW_KEY_F20),
			F21 = new GLFWKeyMapping(Keyboard.F21, GLFW_KEY_F21),
			F22 = new GLFWKeyMapping(Keyboard.F22, GLFW_KEY_F22),
			F23 = new GLFWKeyMapping(Keyboard.F23, GLFW_KEY_F23),
			F24 = new GLFWKeyMapping(Keyboard.F24, GLFW_KEY_F24),
			F25 = new GLFWKeyMapping(Keyboard.F25, GLFW_KEY_F25),
			KP_0 = new GLFWKeyMapping(Keyboard.KP_0, GLFW_KEY_KP_0),
			KP_1 = new GLFWKeyMapping(Keyboard.KP_1, GLFW_KEY_KP_1),
			KP_2 = new GLFWKeyMapping(Keyboard.KP_2, GLFW_KEY_KP_2),
			KP_3 = new GLFWKeyMapping(Keyboard.KP_3, GLFW_KEY_KP_3),
			KP_4 = new GLFWKeyMapping(Keyboard.KP_4, GLFW_KEY_KP_4),
			KP_5 = new GLFWKeyMapping(Keyboard.KP_5, GLFW_KEY_KP_5),
			KP_6 = new GLFWKeyMapping(Keyboard.KP_6, GLFW_KEY_KP_6),
			KP_7 = new GLFWKeyMapping(Keyboard.KP_7, GLFW_KEY_KP_7),
			KP_8 = new GLFWKeyMapping(Keyboard.KP_8, GLFW_KEY_KP_8),
			KP_9 = new GLFWKeyMapping(Keyboard.KP_9, GLFW_KEY_KP_9),
			KP_DOT = new GLFWKeyMapping(Keyboard.KP_DOT, GLFW_KEY_KP_DECIMAL),
			KP_DIV = new GLFWKeyMapping(Keyboard.KP_DIV, GLFW_KEY_KP_DIVIDE),
			KP_MUL = new GLFWKeyMapping(Keyboard.KP_MUL, GLFW_KEY_KP_MULTIPLY),
			KP_SUB = new GLFWKeyMapping(Keyboard.KP_SUB, GLFW_KEY_KP_SUBTRACT),
			KP_ADD = new GLFWKeyMapping(Keyboard.KP_ADD, GLFW_KEY_KP_ADD),
			KP_ENTER = new GLFWKeyMapping(Keyboard.KP_ENTER, GLFW_KEY_KP_ENTER),
			KP_EQUAL = new GLFWKeyMapping(Keyboard.KP_EQUAL, GLFW_KEY_KP_EQUAL),
			LEFT_SHIFT = new GLFWKeyMapping(Keyboard.LEFT_SHIFT, GLFW_KEY_LEFT_SHIFT),
			LEFT_CONTROL = new GLFWKeyMapping(Keyboard.LEFT_CONTROL, GLFW_KEY_LEFT_CONTROL),
			LEFT_ALT = new GLFWKeyMapping(Keyboard.LEFT_ALT, GLFW_KEY_LEFT_ALT),
			LEFT_SUPER = new GLFWKeyMapping(Keyboard.LEFT_SUPER, GLFW_KEY_LEFT_SUPER),
			RIGHT_SHIFT = new GLFWKeyMapping(Keyboard.RIGHT_SHIFT, GLFW_KEY_RIGHT_SHIFT),
			RIGHT_CONTROL = new GLFWKeyMapping(Keyboard.RIGHT_CONTROL, GLFW_KEY_RIGHT_CONTROL),
			RIGHT_ALT = new GLFWKeyMapping(Keyboard.RIGHT_ALT, GLFW_KEY_RIGHT_ALT),
			RIGHT_SUPER = new GLFWKeyMapping(Keyboard.RIGHT_SUPER, GLFW_KEY_RIGHT_SUPER),
			MENU = new GLFWKeyMapping(Keyboard.MENU, GLFW_KEY_MENU);
	/* @formatter: on */

	public GLFWKeyboardAdapter(long ptr_glfwWindow) {
		super(ptr_glfwWindow);
	}

	@Override
	public boolean isConnected() {
		return true; /* keyboard is always connected */
	}

	@FeatureAdapter
	public void isPressed(GLFWKeyMapping mapping, Button1b button) {
		int status = glfwGetKey(ptr_glfwWindow, mapping.glfwKey);
		button.pressed = status >= GLFW_PRESS;
	}

	@Override
	public void poll() {
		/* nothing to poll */
	}

}
