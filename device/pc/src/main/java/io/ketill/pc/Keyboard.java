package io.ketill.pc;

import io.ketill.AdapterSupplier;
import io.ketill.FeaturePresent;
import io.ketill.FeatureState;
import io.ketill.IoDevice;
import io.ketill.ToStringUtils;
import io.ketill.pressable.PressableIoFeatureConfig;
import io.ketill.pressable.PressableIoFeatureConfigView;
import io.ketill.pressable.PressableIoFeatureSupport;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A generic computer keyboard.
 *
 * @see Mouse
 */
public class Keyboard extends IoDevice implements PressableIoFeatureSupport {

    /* @formatter:off */
    @FeaturePresent /* printable keys */
    public static final @NotNull KeyboardKey
            KEY_SPACE = new KeyboardKey("space"),
            KEY_APOSTROPHE = new KeyboardKey("apostrophe"),
            KEY_COMMA = new KeyboardKey("comma"),
            KEY_MINUS = new KeyboardKey("minus"),
            KEY_PERIOD = new KeyboardKey("period"),
            KEY_SLASH = new KeyboardKey("slash"),
            KEY_ZERO = new KeyboardKey("0"),
            KEY_ONE = new KeyboardKey("1"), 
            KEY_TWO = new KeyboardKey("2"),
            KEY_THREE = new KeyboardKey("3"),
            KEY_FOUR = new KeyboardKey("4"),
            KEY_FIVE = new KeyboardKey("5"),
            KEY_SIX = new KeyboardKey("6"),
            KEY_SEVEN = new KeyboardKey("7"),
            KEY_EIGHT = new KeyboardKey("8"),
            KEY_NINE = new KeyboardKey("9"),
            KEY_SEMICOLON = new KeyboardKey("semicolon"),
            KEY_EQUAL = new KeyboardKey("equal"),
            KEY_A = new KeyboardKey("a"),
            KEY_B = new KeyboardKey("b"),
            KEY_C = new KeyboardKey("c"),
            KEY_D = new KeyboardKey("d"),
            KEY_E = new KeyboardKey("e"),
            KEY_F = new KeyboardKey("f"),
            KEY_G = new KeyboardKey("g"),
            KEY_H = new KeyboardKey("h"),
            KEY_I = new KeyboardKey("i"),
            KEY_J = new KeyboardKey("j"),
            KEY_K = new KeyboardKey("k"),
            KEY_L = new KeyboardKey("l"),
            KEY_M = new KeyboardKey("m"),
            KEY_N = new KeyboardKey("n"),
            KEY_O = new KeyboardKey("o"),
            KEY_P = new KeyboardKey("p"),
            KEY_Q = new KeyboardKey("q"),
            KEY_R = new KeyboardKey("r"),
            KEY_S = new KeyboardKey("s"),
            KEY_T = new KeyboardKey("t"),
            KEY_U = new KeyboardKey("u"),
            KEY_V = new KeyboardKey("v"),
            KEY_W = new KeyboardKey("w"),
            KEY_X = new KeyboardKey("x"),
            KEY_Y = new KeyboardKey("y"),
            KEY_Z = new KeyboardKey("z"),
            KEY_LEFT_BRACKET = new KeyboardKey("left_bracket"),
            KEY_BACKSLASH = new KeyboardKey("backslash"),
            KEY_RIGHT_BRACKET = new KeyboardKey("right_bracket"),
            KEY_GRAVE_ACCENT = new KeyboardKey("grave_accent"),
            KEY_WORLD_1 = new KeyboardKey("world_1"),
            KEY_WORLD_2 = new KeyboardKey("world_2");

    @FeaturePresent /* method keys */
    public static final @NotNull KeyboardKey
            KEY_ESCAPE = new KeyboardKey("escape"),
            KEY_ENTER = new KeyboardKey("enter"),
            KEY_TAB = new KeyboardKey("tab"),
            KEY_BACKSPACE = new KeyboardKey("backspace"),
            KEY_INSERT = new KeyboardKey("insert"),
            KEY_DELETE = new KeyboardKey("delete"),
            KEY_RIGHT = new KeyboardKey("right"),
            KEY_LEFT = new KeyboardKey("left"),
            KEY_DOWN = new KeyboardKey("down"),
            KEY_UP = new KeyboardKey("up"),
            KEY_PAGE_UP = new KeyboardKey("page_up"),
            KEY_PAGE_DOWN = new KeyboardKey("page_down"),
            KEY_HOME = new KeyboardKey("home"),
            KEY_END = new KeyboardKey("end"),
            KEY_CAPS_LOCK = new KeyboardKey("caps_lock"),
            KEY_SCROLL_LOCK = new KeyboardKey("scroll_lock"),
            KEY_NUM_LOCK = new KeyboardKey("num_lock"),
            KEY_PRINT_SCREEN = new KeyboardKey("print_screen"),
            KEY_PAUSE = new KeyboardKey("pause"),
            KEY_F1 = new KeyboardKey("f1"),
            KEY_F2 = new KeyboardKey("f2"),
            KEY_F3 = new KeyboardKey("f3"),
            KEY_F4 = new KeyboardKey("f4"),
            KEY_F5 = new KeyboardKey("f5"),
            KEY_F6 = new KeyboardKey("f6"),
            KEY_F7 = new KeyboardKey("f7"),
            KEY_F8 = new KeyboardKey("f8"),
            KEY_F9 = new KeyboardKey("f9"),
            KEY_F10 = new KeyboardKey("f10"),
            KEY_F11 = new KeyboardKey("f11"),
            KEY_F12 = new KeyboardKey("f12"),
            KEY_F13 = new KeyboardKey("f13"),
            KEY_F14 = new KeyboardKey("f14"),
            KEY_F15 = new KeyboardKey("f15"),
            KEY_F16 = new KeyboardKey("f16"),
            KEY_F17 = new KeyboardKey("f17"),
            KEY_F18 = new KeyboardKey("f18"),
            KEY_F19 = new KeyboardKey("f19"),
            KEY_F20 = new KeyboardKey("f20"),
            KEY_F21 = new KeyboardKey("f21"),
            KEY_F22 = new KeyboardKey("f22"),
            KEY_F23 = new KeyboardKey("f23"),
            KEY_F24 = new KeyboardKey("f24"),
            KEY_F25 = new KeyboardKey("f25"),
            KEY_KP_0 = new KeyboardKey("kp_0"),
            KEY_KP_1 = new KeyboardKey("kp_1"),
            KEY_KP_2 = new KeyboardKey("kp_2"),
            KEY_KP_3 = new KeyboardKey("kp_3"),
            KEY_KP_4 = new KeyboardKey("kp_4"),
            KEY_KP_5 = new KeyboardKey("kp_5"),
            KEY_KP_6 = new KeyboardKey("kp_6"),
            KEY_KP_7 = new KeyboardKey("kp_7"),
            KEY_KP_8 = new KeyboardKey("kp_8"),
            KEY_KP_9 = new KeyboardKey("kp_9"),
            KEY_KP_DOT = new KeyboardKey("kp_dot"),
            KEY_KP_DIV = new KeyboardKey("kp_div"),
            KEY_KP_MUL = new KeyboardKey("kp_mul"),
            KEY_KP_SUB = new KeyboardKey("kp_sub"),
            KEY_KP_ADD = new KeyboardKey("kp_add"),
            KEY_KP_ENTER = new KeyboardKey("kp_enter"),
            KEY_KP_EQUAL = new KeyboardKey("kp_equal"),
            KEY_LEFT_SHIFT = new KeyboardKey("left_shift"),
            KEY_LEFT_CTRL = new KeyboardKey("left_ctrl"),
            KEY_LEFT_ALT = new KeyboardKey("left_alt"),
            KEY_LEFT_SUPER = new KeyboardKey("left_super"),
            KEY_RIGHT_SHIFT = new KeyboardKey("right_shift"),
            KEY_RIGHT_CTRL = new KeyboardKey("right_ctrl"),
            KEY_RIGHT_ALT = new KeyboardKey("right_alt"),
            KEY_RIGHT_SUPER = new KeyboardKey("right_super"),
            KEY_MENU = new KeyboardKey("menu");
    /* @formatter:on */

    /* @formatter:off */
    @FeatureState /* printable keys */
    public final @NotNull KeyPress
            space = this.getState(KEY_SPACE),
            apostrophe = this.getState(KEY_APOSTROPHE),
            comma = this.getState(KEY_COMMA),
            minus = this.getState(KEY_MINUS),
            period = this.getState(KEY_PERIOD),
            slash = this.getState(KEY_SLASH),
            zero = this.getState(KEY_ZERO),
            one = this.getState(KEY_ONE),
            two = this.getState(KEY_TWO),
            three = this.getState(KEY_THREE),
            four = this.getState(KEY_FOUR),
            five = this.getState(KEY_FIVE),
            six = this.getState(KEY_SIX),
            seven = this.getState(KEY_SEVEN),
            eight = this.getState(KEY_EIGHT),
            nine = this.getState(KEY_NINE),
            semicolon = this.getState(KEY_SEMICOLON),
            equal = this.getState(KEY_EQUAL),
            a = this.getState(KEY_A),
            b = this.getState(KEY_B),
            c = this.getState(KEY_C),
            d = this.getState(KEY_D),
            e = this.getState(KEY_E),
            f = this.getState(KEY_F),
            g = this.getState(KEY_G),
            h = this.getState(KEY_H),
            i = this.getState(KEY_I),
            j = this.getState(KEY_J),
            k = this.getState(KEY_K),
            l = this.getState(KEY_L),
            m = this.getState(KEY_M),
            n = this.getState(KEY_N),
            o = this.getState(KEY_O),
            p = this.getState(KEY_P),
            q = this.getState(KEY_Q),
            r = this.getState(KEY_R),
            s = this.getState(KEY_S),
            t = this.getState(KEY_T),
            u = this.getState(KEY_U),
            v = this.getState(KEY_V),
            w = this.getState(KEY_W),
            x = this.getState(KEY_X),
            y = this.getState(KEY_Y),
            z = this.getState(KEY_Z),
            leftBracket = this.getState(KEY_LEFT_BRACKET),
            backslash = this.getState(KEY_BACKSLASH),
            rightBracket = this.getState(KEY_RIGHT_BRACKET),
            graveAccent = this.getState(KEY_GRAVE_ACCENT),
            world1 = this.getState(KEY_WORLD_1),
            world2 = this.getState(KEY_WORLD_2);

    @FeatureState /* method keys */
    public final @NotNull KeyPress
            escape = this.getState(KEY_ESCAPE),
            enter = this.getState(KEY_ENTER),
            tab = this.getState(KEY_TAB),
            backspace = this.getState(KEY_BACKSPACE),
            insert = this.getState(KEY_INSERT),
            delete = this.getState(KEY_DELETE),
            right = this.getState(KEY_RIGHT),
            left = this.getState(KEY_LEFT),
            down = this.getState(KEY_DOWN),
            up = this.getState(KEY_UP),
            pageUp = this.getState(KEY_PAGE_UP),
            pageDown = this.getState(KEY_PAGE_DOWN),
            home = this.getState(KEY_HOME),
            end = this.getState(KEY_END),
            capsLock = this.getState(KEY_CAPS_LOCK),
            scrollLock = this.getState(KEY_SCROLL_LOCK),
            numLock = this.getState(KEY_NUM_LOCK),
            printScreen = this.getState(KEY_PRINT_SCREEN),
            pause = this.getState(KEY_PAUSE),
            f1 = this.getState(KEY_F1),
            f2 = this.getState(KEY_F2),
            f3 = this.getState(KEY_F3),
            f4 = this.getState(KEY_F4),
            f5 = this.getState(KEY_F5),
            f6 = this.getState(KEY_F6),
            f7 = this.getState(KEY_F7),
            f8 = this.getState(KEY_F8),
            f9 = this.getState(KEY_F9),
            f10 = this.getState(KEY_F10),
            f11 = this.getState(KEY_F11),
            f12 = this.getState(KEY_F12),
            f13 = this.getState(KEY_F13),
            f14 = this.getState(KEY_F14),
            f15 = this.getState(KEY_F15),
            f16 = this.getState(KEY_F16),
            f17 = this.getState(KEY_F17),
            f18 = this.getState(KEY_F18),
            f19 = this.getState(KEY_F19),
            f20 = this.getState(KEY_F20),
            f21 = this.getState(KEY_F21),
            f22 = this.getState(KEY_F22),
            f23 = this.getState(KEY_F23),
            f24 = this.getState(KEY_F24),
            f25 = this.getState(KEY_F25),
            kp0 = this.getState(KEY_KP_0),
            kp1 = this.getState(KEY_KP_1),
            kp2 = this.getState(KEY_KP_2),
            kp3 = this.getState(KEY_KP_3),
            kp4 = this.getState(KEY_KP_4),
            kp5 = this.getState(KEY_KP_5),
            kp6 = this.getState(KEY_KP_6),
            kp7 = this.getState(KEY_KP_7),
            kp8 = this.getState(KEY_KP_8),
            kp9 = this.getState(KEY_KP_9),
            kpDot = this.getState(KEY_KP_DOT),
            kpDiv = this.getState(KEY_KP_DIV),
            kpMul = this.getState(KEY_KP_MUL),
            kpSub = this.getState(KEY_KP_SUB),
            kpAdd = this.getState(KEY_KP_ADD),
            kpEnter = this.getState(KEY_KP_ENTER),
            kpEqual = this.getState(KEY_KP_EQUAL),
            leftShift = this.getState(KEY_LEFT_SHIFT),
            leftCtrl = this.getState(KEY_LEFT_CTRL),
            leftAlt = this.getState(KEY_LEFT_ALT),
            leftSuper = this.getState(KEY_LEFT_SUPER),
            rightShift = this.getState(KEY_RIGHT_SHIFT),
            rightCtrl = this.getState(KEY_RIGHT_CTRL),
            rightAlt = this.getState(KEY_RIGHT_ALT),
            rightSuper = this.getState(KEY_RIGHT_SUPER),
            menu = this.getState(KEY_MENU);
    /* @formatter:on */

    private @NotNull PressableIoFeatureConfigView pressableConfig;

    /**
     * Constructs a new {@code Keyboard}.
     *
     * @param adapterSupplier the keyboard adapter supplier.
     * @throws NullPointerException if {@code adapterSupplier} is
     *                              {@code null}; if the adapter given by
     *                              {@code adapterSupplier} is {@code null}.
     */
    public Keyboard(@NotNull AdapterSupplier<Keyboard> adapterSupplier) {
        super("keyboard", adapterSupplier);
        this.pressableConfig = PressableIoFeatureConfig.DEFAULT;
    }

    @Override
    public final @NotNull PressableIoFeatureConfigView getPressableConfig() {
        return this.pressableConfig;
    }

    @Override
    public final void usePressableConfig(@Nullable PressableIoFeatureConfigView view) {
        this.pressableConfig = PressableIoFeatureConfig.valueOf(view);
    }

    /* @formatter:off */
    @Override
    public String toString() {
        return ToStringUtils.getJoiner(super.toString(), this)
                .add("pressableConfig=" + pressableConfig)
                .toString();
    }
    /* @formatter:on */

}
