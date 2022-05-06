package io.ketill.pc;

import io.ketill.pressable.PressableFeatureConfig;
import io.ketill.pressable.PressableFeatureEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static io.ketill.KetillAssertions.*;
import static io.ketill.pc.Keyboard.*;
import static org.junit.jupiter.api.Assertions.*;

class KeyboardTest {

    private Keyboard keyboard;

    @BeforeEach
    void createKeyboard() {
        this.keyboard = new Keyboard(MockPcAdapter::new);
    }

    @Test
    void ensureAllStatesValid() {
        /* @formatter:off */
        assertFeatureOwnsState(keyboard, keyboard.space, KEY_SPACE);
        assertFeatureOwnsState(keyboard, keyboard.apostrophe, KEY_APOSTROPHE);
        assertFeatureOwnsState(keyboard, keyboard.comma, KEY_COMMA);
        assertFeatureOwnsState(keyboard, keyboard.minus, KEY_MINUS);
        assertFeatureOwnsState(keyboard, keyboard.period, KEY_PERIOD);
        assertFeatureOwnsState(keyboard, keyboard.slash, KEY_SLASH);
        assertFeatureOwnsState(keyboard, keyboard.zero, KEY_ZERO);
        assertFeatureOwnsState(keyboard, keyboard.one, KEY_ONE);
        assertFeatureOwnsState(keyboard, keyboard.two, KEY_TWO);
        assertFeatureOwnsState(keyboard, keyboard.three, KEY_THREE);
        assertFeatureOwnsState(keyboard, keyboard.four, KEY_FOUR);
        assertFeatureOwnsState(keyboard, keyboard.five, KEY_FIVE);
        assertFeatureOwnsState(keyboard, keyboard.six, KEY_SIX);
        assertFeatureOwnsState(keyboard, keyboard.seven, KEY_SEVEN);
        assertFeatureOwnsState(keyboard, keyboard.eight, KEY_EIGHT);
        assertFeatureOwnsState(keyboard, keyboard.nine, KEY_NINE);
        assertFeatureOwnsState(keyboard, keyboard.semicolon, KEY_SEMICOLON);
        assertFeatureOwnsState(keyboard, keyboard.equal, KEY_EQUAL);
        assertFeatureOwnsState(keyboard, keyboard.a, KEY_A);
        assertFeatureOwnsState(keyboard, keyboard.b, KEY_B);
        assertFeatureOwnsState(keyboard, keyboard.c, KEY_C);
        assertFeatureOwnsState(keyboard, keyboard.d, KEY_D);
        assertFeatureOwnsState(keyboard, keyboard.e, KEY_E);
        assertFeatureOwnsState(keyboard, keyboard.f, KEY_F);
        assertFeatureOwnsState(keyboard, keyboard.g, KEY_G);
        assertFeatureOwnsState(keyboard, keyboard.h, KEY_H);
        assertFeatureOwnsState(keyboard, keyboard.i, KEY_I);
        assertFeatureOwnsState(keyboard, keyboard.j, KEY_J);
        assertFeatureOwnsState(keyboard, keyboard.k, KEY_K);
        assertFeatureOwnsState(keyboard, keyboard.l, KEY_L);
        assertFeatureOwnsState(keyboard, keyboard.m, KEY_M);
        assertFeatureOwnsState(keyboard, keyboard.n, KEY_N);
        assertFeatureOwnsState(keyboard, keyboard.o, KEY_O);
        assertFeatureOwnsState(keyboard, keyboard.p, KEY_P);
        assertFeatureOwnsState(keyboard, keyboard.q, KEY_Q);
        assertFeatureOwnsState(keyboard, keyboard.r, KEY_R);
        assertFeatureOwnsState(keyboard, keyboard.s, KEY_S);
        assertFeatureOwnsState(keyboard, keyboard.t, KEY_T);
        assertFeatureOwnsState(keyboard, keyboard.u, KEY_U);
        assertFeatureOwnsState(keyboard, keyboard.v, KEY_V);
        assertFeatureOwnsState(keyboard, keyboard.w, KEY_W);
        assertFeatureOwnsState(keyboard, keyboard.x, KEY_X);
        assertFeatureOwnsState(keyboard, keyboard.y, KEY_Y);
        assertFeatureOwnsState(keyboard, keyboard.z, KEY_Z);
        assertFeatureOwnsState(keyboard, keyboard.leftBracket, KEY_LEFT_BRACKET);
        assertFeatureOwnsState(keyboard, keyboard.backslash, KEY_BACKSLASH);
        assertFeatureOwnsState(keyboard, keyboard.rightBracket, KEY_RIGHT_BRACKET);
        assertFeatureOwnsState(keyboard, keyboard.graveAccent, KEY_GRAVE_ACCENT);
        assertFeatureOwnsState(keyboard, keyboard.world1, KEY_WORLD_1);
        assertFeatureOwnsState(keyboard, keyboard.world2, KEY_WORLD_2);
        /* @formatter:on */

        /* @formatter:off */
        assertFeatureOwnsState(keyboard, keyboard.escape, KEY_ESCAPE);
        assertFeatureOwnsState(keyboard, keyboard.enter, KEY_ENTER);
        assertFeatureOwnsState(keyboard, keyboard.tab, KEY_TAB);
        assertFeatureOwnsState(keyboard, keyboard.backspace, KEY_BACKSPACE);
        assertFeatureOwnsState(keyboard, keyboard.insert, KEY_INSERT);
        assertFeatureOwnsState(keyboard, keyboard.delete, KEY_DELETE);
        assertFeatureOwnsState(keyboard, keyboard.right, KEY_RIGHT);
        assertFeatureOwnsState(keyboard, keyboard.left, KEY_LEFT);
        assertFeatureOwnsState(keyboard, keyboard.down, KEY_DOWN);
        assertFeatureOwnsState(keyboard, keyboard.up, KEY_UP);
        assertFeatureOwnsState(keyboard, keyboard.pageUp, KEY_PAGE_UP);
        assertFeatureOwnsState(keyboard, keyboard.pageDown, KEY_PAGE_DOWN);
        assertFeatureOwnsState(keyboard, keyboard.home, KEY_HOME);
        assertFeatureOwnsState(keyboard, keyboard.end, KEY_END);
        assertFeatureOwnsState(keyboard, keyboard.capsLock, KEY_CAPS_LOCK);
        assertFeatureOwnsState(keyboard, keyboard.scrollLock, KEY_SCROLL_LOCK);
        assertFeatureOwnsState(keyboard, keyboard.numLock, KEY_NUM_LOCK);
        assertFeatureOwnsState(keyboard, keyboard.printScreen, KEY_PRINT_SCREEN);
        assertFeatureOwnsState(keyboard, keyboard.pause, KEY_PAUSE);
        assertFeatureOwnsState(keyboard, keyboard.f1, KEY_F1);
        assertFeatureOwnsState(keyboard, keyboard.f2, KEY_F2);
        assertFeatureOwnsState(keyboard, keyboard.f3, KEY_F3);
        assertFeatureOwnsState(keyboard, keyboard.f4, KEY_F4);
        assertFeatureOwnsState(keyboard, keyboard.f5, KEY_F5);
        assertFeatureOwnsState(keyboard, keyboard.f6, KEY_F6);
        assertFeatureOwnsState(keyboard, keyboard.f7, KEY_F7);
        assertFeatureOwnsState(keyboard, keyboard.f8, KEY_F8);
        assertFeatureOwnsState(keyboard, keyboard.f9, KEY_F9);
        assertFeatureOwnsState(keyboard, keyboard.f10, KEY_F10);
        assertFeatureOwnsState(keyboard, keyboard.f11, KEY_F11);
        assertFeatureOwnsState(keyboard, keyboard.f12, KEY_F12);
        assertFeatureOwnsState(keyboard, keyboard.f13, KEY_F13);
        assertFeatureOwnsState(keyboard, keyboard.f14, KEY_F14);
        assertFeatureOwnsState(keyboard, keyboard.f15, KEY_F15);
        assertFeatureOwnsState(keyboard, keyboard.f16, KEY_F16);
        assertFeatureOwnsState(keyboard, keyboard.f17, KEY_F17);
        assertFeatureOwnsState(keyboard, keyboard.f18, KEY_F18);
        assertFeatureOwnsState(keyboard, keyboard.f19, KEY_F19);
        assertFeatureOwnsState(keyboard, keyboard.f20, KEY_F20);
        assertFeatureOwnsState(keyboard, keyboard.f21, KEY_F21);
        assertFeatureOwnsState(keyboard, keyboard.f22, KEY_F22);
        assertFeatureOwnsState(keyboard, keyboard.f23, KEY_F23);
        assertFeatureOwnsState(keyboard, keyboard.f24, KEY_F24);
        assertFeatureOwnsState(keyboard, keyboard.f25, KEY_F25);
        assertFeatureOwnsState(keyboard, keyboard.kp0, KEY_KP_0);
        assertFeatureOwnsState(keyboard, keyboard.kp1, KEY_KP_1);
        assertFeatureOwnsState(keyboard, keyboard.kp2, KEY_KP_2);
        assertFeatureOwnsState(keyboard, keyboard.kp3, KEY_KP_3);
        assertFeatureOwnsState(keyboard, keyboard.kp4, KEY_KP_4);
        assertFeatureOwnsState(keyboard, keyboard.kp5, KEY_KP_5);
        assertFeatureOwnsState(keyboard, keyboard.kp6, KEY_KP_6);
        assertFeatureOwnsState(keyboard, keyboard.kp7, KEY_KP_7);
        assertFeatureOwnsState(keyboard, keyboard.kp8, KEY_KP_8);
        assertFeatureOwnsState(keyboard, keyboard.kp9, KEY_KP_9);
        assertFeatureOwnsState(keyboard, keyboard.kpDot, KEY_KP_DOT);
        assertFeatureOwnsState(keyboard, keyboard.kpDiv, KEY_KP_DIV);
        assertFeatureOwnsState(keyboard, keyboard.kpMul, KEY_KP_MUL);
        assertFeatureOwnsState(keyboard, keyboard.kpSub, KEY_KP_SUB);
        assertFeatureOwnsState(keyboard, keyboard.kpAdd, KEY_KP_ADD);
        assertFeatureOwnsState(keyboard, keyboard.kpEnter, KEY_KP_ENTER);
        assertFeatureOwnsState(keyboard, keyboard.kpEqual, KEY_KP_EQUAL);
        assertFeatureOwnsState(keyboard, keyboard.leftShift, KEY_LEFT_SHIFT);
        assertFeatureOwnsState(keyboard, keyboard.leftCtrl, KEY_LEFT_CTRL);
        assertFeatureOwnsState(keyboard, keyboard.leftAlt, KEY_LEFT_ALT);
        assertFeatureOwnsState(keyboard, keyboard.leftSuper, KEY_LEFT_SUPER);
        assertFeatureOwnsState(keyboard, keyboard.rightShift, KEY_RIGHT_SHIFT);
        assertFeatureOwnsState(keyboard, keyboard.rightCtrl, KEY_RIGHT_CTRL);
        assertFeatureOwnsState(keyboard, keyboard.rightAlt, KEY_RIGHT_ALT);
        assertFeatureOwnsState(keyboard, keyboard.rightSuper, KEY_RIGHT_SUPER);
        assertFeatureOwnsState(keyboard, keyboard.menu, KEY_MENU);
        /* @formatter:on */
    }

    @Test
    void testKeyboardKeyEvents() {
        /* set callback for next test */
        AtomicBoolean notified = new AtomicBoolean();
        keyboard.onPressableEvent(e -> notified.set(true));

        /*
         * When a keyboard key is registered, the keyboard should notify
         * listeners when it is pressed or released.
         */
        KeyboardKey key = new KeyboardKey("key");
        keyboard.registerFeature(key);
        KeyPressZ state = keyboard.getInternalState(key);

        state.pressed = true; /* press key */
        keyboard.poll(); /* fire events */
        assertTrue(notified.get());

        /*
         * When a keyboard key is unregistered, the keyboard should no
         * longer notify listeners when it is pressed or released.
         */
        keyboard.unregisterFeature(key);
        notified.set(false);

        state.pressed = false; /* release key */
        keyboard.poll(); /* fire events */
        assertFalse(notified.get());
    }

    @Test
    void testGetPressableCallback() {
        assertNull(keyboard.getPressableCallback());
    }

    @Test
    void testUsePressableCallback() {
        /* @formatter:off */
        Consumer<PressableFeatureEvent> callback = (e) -> {};
        keyboard.onPressableEvent(callback);
        assertSame(callback, keyboard.getPressableCallback());
        /* @formatter:on */
    }

    @Test
    void testUsePressableConfig() {
        PressableFeatureConfig config = new PressableFeatureConfig();
        keyboard.usePressableConfig(config);
        assertSame(config, keyboard.getPressableConfig());

        /*
         * When the keyboard is told to use a null value for the pressable
         * feature config, it should use the default configuration instead.
         */
        keyboard.usePressableConfig(null);
        assertSame(PressableFeatureConfig.DEFAULT,
                keyboard.getPressableConfig());
    }

    @Test
    void testGetPressableConfig() {
        assertSame(PressableFeatureConfig.DEFAULT,
                keyboard.getPressableConfig());
    }

}
