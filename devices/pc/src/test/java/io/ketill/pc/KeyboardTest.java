package io.ketill.pc;

import io.ketill.IoFeature;
import io.ketill.pressable.PressableFeatureConfig;
import io.ketill.pressable.PressableFeatureEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.ketill.pc.Keyboard.*;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardTest {

    private Keyboard keyboard;

    private void assertStateIsFeature(Object state, IoFeature<?> feature) {
        assertSame(state, keyboard.getState(feature));
    }

    @BeforeEach
    void setup() {
        this.keyboard = new Keyboard(MockPcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        assertStateIsFeature(keyboard.space, KEY_SPACE);
        assertStateIsFeature(keyboard.apostrophe, KEY_APOSTROPHE);
        assertStateIsFeature(keyboard.comma, KEY_COMMA);
        assertStateIsFeature(keyboard.minus, KEY_MINUS);
        assertStateIsFeature(keyboard.period, KEY_PERIOD);
        assertStateIsFeature(keyboard.slash, KEY_SLASH);
        assertStateIsFeature(keyboard.zero, KEY_ZERO);
        assertStateIsFeature(keyboard.one, KEY_ONE);
        assertStateIsFeature(keyboard.two, KEY_TWO);
        assertStateIsFeature(keyboard.three, KEY_THREE);
        assertStateIsFeature(keyboard.four, KEY_FOUR);
        assertStateIsFeature(keyboard.five, KEY_FIVE);
        assertStateIsFeature(keyboard.six, KEY_SIX);
        assertStateIsFeature(keyboard.seven, KEY_SEVEN);
        assertStateIsFeature(keyboard.eight, KEY_EIGHT);
        assertStateIsFeature(keyboard.nine, KEY_NINE);
        assertStateIsFeature(keyboard.semicolon, KEY_SEMICOLON);
        assertStateIsFeature(keyboard.equal, KEY_EQUAL);
        assertStateIsFeature(keyboard.a, KEY_A);
        assertStateIsFeature(keyboard.b, KEY_B);
        assertStateIsFeature(keyboard.c, KEY_C);
        assertStateIsFeature(keyboard.d, KEY_D);
        assertStateIsFeature(keyboard.e, KEY_E);
        assertStateIsFeature(keyboard.f, KEY_F);
        assertStateIsFeature(keyboard.g, KEY_G);
        assertStateIsFeature(keyboard.h, KEY_H);
        assertStateIsFeature(keyboard.i, KEY_I);
        assertStateIsFeature(keyboard.j, KEY_J);
        assertStateIsFeature(keyboard.k, KEY_K);
        assertStateIsFeature(keyboard.l, KEY_L);
        assertStateIsFeature(keyboard.m, KEY_M);
        assertStateIsFeature(keyboard.n, KEY_N);
        assertStateIsFeature(keyboard.o, KEY_O);
        assertStateIsFeature(keyboard.p, KEY_P);
        assertStateIsFeature(keyboard.q, KEY_Q);
        assertStateIsFeature(keyboard.r, KEY_R);
        assertStateIsFeature(keyboard.s, KEY_S);
        assertStateIsFeature(keyboard.t, KEY_T);
        assertStateIsFeature(keyboard.u, KEY_U);
        assertStateIsFeature(keyboard.v, KEY_V);
        assertStateIsFeature(keyboard.w, KEY_W);
        assertStateIsFeature(keyboard.x, KEY_X);
        assertStateIsFeature(keyboard.y, KEY_Y);
        assertStateIsFeature(keyboard.z, KEY_Z);
        assertStateIsFeature(keyboard.leftBracket, KEY_LEFT_BRACKET);
        assertStateIsFeature(keyboard.backslash, KEY_BACKSLASH);
        assertStateIsFeature(keyboard.rightBracket, KEY_RIGHT_BRACKET);
        assertStateIsFeature(keyboard.graveAccent, KEY_GRAVE_ACCENT);
        assertStateIsFeature(keyboard.world1, KEY_WORLD_1);
        assertStateIsFeature(keyboard.world2, KEY_WORLD_2);

        assertStateIsFeature(keyboard.escape, KEY_ESCAPE);
        assertStateIsFeature(keyboard.enter, KEY_ENTER);
        assertStateIsFeature(keyboard.tab, KEY_TAB);
        assertStateIsFeature(keyboard.backspace, KEY_BACKSPACE);
        assertStateIsFeature(keyboard.insert, KEY_INSERT);
        assertStateIsFeature(keyboard.delete, KEY_DELETE);
        assertStateIsFeature(keyboard.right, KEY_RIGHT);
        assertStateIsFeature(keyboard.left, KEY_LEFT);
        assertStateIsFeature(keyboard.down, KEY_DOWN);
        assertStateIsFeature(keyboard.up, KEY_UP);
        assertStateIsFeature(keyboard.pageUp, KEY_PAGE_UP);
        assertStateIsFeature(keyboard.pageDown, KEY_PAGE_DOWN);
        assertStateIsFeature(keyboard.home, KEY_HOME);
        assertStateIsFeature(keyboard.end, KEY_END);
        assertStateIsFeature(keyboard.capsLock, KEY_CAPS_LOCK);
        assertStateIsFeature(keyboard.scrollLock, KEY_SCROLL_LOCK);
        assertStateIsFeature(keyboard.numLock, KEY_NUM_LOCK);
        assertStateIsFeature(keyboard.printScreen, KEY_PRINT_SCREEN);
        assertStateIsFeature(keyboard.pause, KEY_PAUSE);
        assertStateIsFeature(keyboard.f1, KEY_F1);
        assertStateIsFeature(keyboard.f2, KEY_F2);
        assertStateIsFeature(keyboard.f3, KEY_F3);
        assertStateIsFeature(keyboard.f4, KEY_F4);
        assertStateIsFeature(keyboard.f5, KEY_F5);
        assertStateIsFeature(keyboard.f6, KEY_F6);
        assertStateIsFeature(keyboard.f7, KEY_F7);
        assertStateIsFeature(keyboard.f8, KEY_F8);
        assertStateIsFeature(keyboard.f9, KEY_F9);
        assertStateIsFeature(keyboard.f10, KEY_F10);
        assertStateIsFeature(keyboard.f11, KEY_F11);
        assertStateIsFeature(keyboard.f12, KEY_F12);
        assertStateIsFeature(keyboard.f13, KEY_F13);
        assertStateIsFeature(keyboard.f14, KEY_F14);
        assertStateIsFeature(keyboard.f15, KEY_F15);
        assertStateIsFeature(keyboard.f16, KEY_F16);
        assertStateIsFeature(keyboard.f17, KEY_F17);
        assertStateIsFeature(keyboard.f18, KEY_F18);
        assertStateIsFeature(keyboard.f19, KEY_F19);
        assertStateIsFeature(keyboard.f20, KEY_F20);
        assertStateIsFeature(keyboard.f21, KEY_F21);
        assertStateIsFeature(keyboard.f22, KEY_F22);
        assertStateIsFeature(keyboard.f23, KEY_F23);
        assertStateIsFeature(keyboard.f24, KEY_F24);
        assertStateIsFeature(keyboard.f25, KEY_F25);
        assertStateIsFeature(keyboard.kp0, KEY_KP_0);
        assertStateIsFeature(keyboard.kp1, KEY_KP_1);
        assertStateIsFeature(keyboard.kp2, KEY_KP_2);
        assertStateIsFeature(keyboard.kp3, KEY_KP_3);
        assertStateIsFeature(keyboard.kp4, KEY_KP_4);
        assertStateIsFeature(keyboard.kp5, KEY_KP_5);
        assertStateIsFeature(keyboard.kp6, KEY_KP_6);
        assertStateIsFeature(keyboard.kp7, KEY_KP_7);
        assertStateIsFeature(keyboard.kp8, KEY_KP_8);
        assertStateIsFeature(keyboard.kp9, KEY_KP_9);
        assertStateIsFeature(keyboard.kpDot, KEY_KP_DOT);
        assertStateIsFeature(keyboard.kpDiv, KEY_KP_DIV);
        assertStateIsFeature(keyboard.kpMul, KEY_KP_MUL);
        assertStateIsFeature(keyboard.kpSub, KEY_KP_SUB);
        assertStateIsFeature(keyboard.kpAdd, KEY_KP_ADD);
        assertStateIsFeature(keyboard.kpEnter, KEY_KP_ENTER);
        assertStateIsFeature(keyboard.kpEqual, KEY_KP_EQUAL);
        assertStateIsFeature(keyboard.leftShift, KEY_LEFT_SHIFT);
        assertStateIsFeature(keyboard.leftCtrl, KEY_LEFT_CTRL);
        assertStateIsFeature(keyboard.leftAlt, KEY_LEFT_ALT);
        assertStateIsFeature(keyboard.leftSuper, KEY_LEFT_SUPER);
        assertStateIsFeature(keyboard.rightShift, KEY_RIGHT_SHIFT);
        assertStateIsFeature(keyboard.rightCtrl, KEY_RIGHT_CTRL);
        assertStateIsFeature(keyboard.rightAlt, KEY_RIGHT_ALT);
        assertStateIsFeature(keyboard.rightSuper, KEY_RIGHT_SUPER);
        assertStateIsFeature(keyboard.menu, KEY_MENU);
    }

    @Test
    void onKeyboardKeyEvent() {
        /* set callback for next test */
        AtomicBoolean notified = new AtomicBoolean();
        keyboard.onPressableEvent(e -> notified.set(true));

        /*
         * When a keyboard key is registered to a keyboard,
         * the keyboard is expected to notify listeners when
         * said key is pressed or released.
         */
        KeyboardKey key = new KeyboardKey("key");
        Key1b state = keyboard.registerFeature(key).state;

        state.pressed = true; /* press key */
        keyboard.poll(); /* fire events */
        assertTrue(notified.get());

        /*
         * When a keyboard key unregistered from a keyboard,
         * the keyboard should no longer notify listeners
         * when said key is pressed or released.
         */
        keyboard.unregisterFeature(key);
        notified.set(false);

        state.pressed = false; /* release key */
        keyboard.poll(); /* fire events */
        assertFalse(notified.get());
    }

    @Test
    void getPressableCallback() {
        /*
         * By default, the keyboard should have no pressable
         * feature callback set unless one is provided by the
         * user via onPressableEvent().
         */
        assertNull(keyboard.getPressableCallback());
    }

    @Test
    void usePressableCallback() {
        /*
         * After setting the pressable feature callback,
         * the keyboard should use the one provided by
         * the user.
         */
        /* @formatter:off */
        Consumer<PressableFeatureEvent> callback = (e) -> {};
        keyboard.onPressableEvent(callback);
        assertSame(callback, keyboard.getPressableCallback());
        /* @formatter:on */
    }

    @Test
    void usePressableConfig() {
        /*
         * When the keyboard is told to use a not null
         * value for the pressable feature config, it
         * is expected to use the one specified.
         */
        PressableFeatureConfig config = new PressableFeatureConfig();
        keyboard.usePressableConfig(config);
        assertSame(config, keyboard.getPressableConfig());

        /*
         * When the keyboard is told to use a null value
         * for the pressable feature config, it should use
         * the default configuration instead.
         */
        keyboard.usePressableConfig(null);
        assertSame(PressableFeatureConfig.DEFAULT,
                keyboard.getPressableConfig());
    }

    @Test
    void getPressableConfig() {
        /*
         * By default, keyboards should use the default
         * pressable feature config until a user specifies
         * a different one via usePressableConfig().
         */
        assertSame(PressableFeatureConfig.DEFAULT,
                keyboard.getPressableConfig());
    }

}