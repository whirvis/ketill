package io.ketill.awt;

import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.pc.KeyPressZ;
import io.ketill.pc.Keyboard;
import io.ketill.pc.KeyboardKey;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Objects;

import static io.ketill.pc.Keyboard.*;
import static java.awt.event.KeyEvent.*;

public class AwtKeyboardAdapter extends IoDeviceAdapter<Keyboard> {

    private static final int VK_F25 = VK_F24 + 1;

    /**
     * @param component the AWT component.
     * @return the captured keyboard.
     * @throws NullPointerException if {@code component} is {@code null}.
     */
    @CapturingMethod
    public static @NotNull Keyboard capture(@NotNull Component component) {
        Objects.requireNonNull(component, "component cannot be null");
        return new Keyboard((d, r) -> new AwtKeyboardAdapter(d, r, component));
    }

    private final @NotNull AwtKeyboardListener keyboardListener;

    /**
     * @param device    the device which owns this adapter.
     * @param registry  the device's mapped feature registry.
     * @param component the AWT component.
     * @throws NullPointerException if {@code device}, {@code registry},
     *                              or {@code component} are {@code null}.
     */
    public AwtKeyboardAdapter(@NotNull Keyboard device,
                              @NotNull MappedFeatureRegistry registry,
                              @NotNull Component component) {
        super(device, registry);
        Objects.requireNonNull(component, "component cannot be null");
        this.keyboardListener = new AwtKeyboardListener(component);
    }

    /**
     * @param key         the keyboard key to map.
     * @param keyCode     the keycode to map {@code key} to.
     * @param keyLocation the location of the key.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #updateKey(KeyPressZ, KeyMapping)
     */
    @MappingMethod
    protected void mapKey(@NotNull KeyboardKey key, int keyCode,
                          int keyLocation) {
        Objects.requireNonNull(key, "key cannot be null");
        registry.mapFeature(key, new KeyMapping(keyCode, keyLocation),
                this::updateKey);
    }

    /**
     * This method is a shorthand for {@link #mapKey(KeyboardKey, int, int)}
     * with the argument for {@code keyLocation} being
     * {@link KeyEvent#KEY_LOCATION_STANDARD}.
     *
     * @param key     the keyboard key to map.
     * @param keyCode the keycode to map {@code key} to.
     * @throws NullPointerException if {@code key} is {@code null}.
     * @see #updateKey(KeyPressZ, KeyMapping)
     */
    @MappingMethod
    protected final void mapKey(@NotNull KeyboardKey key, int keyCode) {
        Objects.requireNonNull(key, "key cannot be null");
        this.mapKey(key, keyCode, KEY_LOCATION_STANDARD);
    }

    private void mapPrintableKeys() {
        this.mapKey(KEY_SPACE, VK_SPACE);
        this.mapKey(KEY_APOSTROPHE, VK_QUOTE);
        this.mapKey(KEY_COMMA, VK_COMMA);
        this.mapKey(KEY_MINUS, VK_MINUS);
        this.mapKey(KEY_PERIOD, VK_PERIOD);
        this.mapKey(KEY_SLASH, VK_SLASH);
        this.mapKey(KEY_ZERO, VK_0);
        this.mapKey(KEY_ONE, VK_1);
        this.mapKey(KEY_TWO, VK_2);
        this.mapKey(KEY_THREE, VK_3);
        this.mapKey(KEY_FOUR, VK_4);
        this.mapKey(KEY_FIVE, VK_5);
        this.mapKey(KEY_SIX, VK_6);
        this.mapKey(KEY_SEVEN, VK_7);
        this.mapKey(KEY_EIGHT, VK_8);
        this.mapKey(KEY_NINE, VK_9);
        this.mapKey(KEY_SEMICOLON, VK_SEMICOLON);
        this.mapKey(KEY_EQUAL, VK_EQUALS);
        this.mapKey(KEY_A, VK_A);
        this.mapKey(KEY_B, VK_B);
        this.mapKey(KEY_C, VK_C);
        this.mapKey(KEY_D, VK_D);
        this.mapKey(KEY_E, VK_E);
        this.mapKey(KEY_F, VK_F);
        this.mapKey(KEY_G, VK_G);
        this.mapKey(KEY_H, VK_H);
        this.mapKey(KEY_I, VK_I);
        this.mapKey(KEY_J, VK_J);
        this.mapKey(KEY_K, VK_K);
        this.mapKey(KEY_L, VK_L);
        this.mapKey(KEY_M, VK_M);
        this.mapKey(KEY_N, VK_N);
        this.mapKey(KEY_O, VK_O);
        this.mapKey(KEY_P, VK_P);
        this.mapKey(KEY_Q, VK_Q);
        this.mapKey(KEY_R, VK_R);
        this.mapKey(KEY_S, VK_S);
        this.mapKey(KEY_T, VK_T);
        this.mapKey(KEY_U, VK_U);
        this.mapKey(KEY_V, VK_V);
        this.mapKey(KEY_W, VK_W);
        this.mapKey(KEY_X, VK_X);
        this.mapKey(KEY_Y, VK_Y);
        this.mapKey(KEY_Z, VK_Z);
        this.mapKey(KEY_LEFT_BRACKET, VK_OPEN_BRACKET);
        this.mapKey(KEY_BACKSLASH, VK_BACK_SLASH);
        this.mapKey(KEY_RIGHT_BRACKET, VK_CLOSE_BRACKET);
        this.mapKey(KEY_GRAVE_ACCENT, VK_BACK_QUOTE);
    }

    private void mapMethodKeys() {
        this.mapKey(KEY_ESCAPE, VK_ESCAPE);
        this.mapKey(KEY_ENTER, VK_ENTER);
        this.mapKey(KEY_TAB, VK_TAB);
        this.mapKey(KEY_BACKSPACE, VK_BACK_SPACE);
        this.mapKey(KEY_INSERT, VK_INSERT);
        this.mapKey(KEY_DELETE, VK_DELETE);
        this.mapKey(KEY_RIGHT, VK_RIGHT);
        this.mapKey(KEY_LEFT, VK_LEFT);
        this.mapKey(KEY_DOWN, VK_DOWN);
        this.mapKey(KEY_UP, VK_UP);
        this.mapKey(KEY_PAGE_UP, VK_PAGE_UP);
        this.mapKey(KEY_PAGE_DOWN, VK_PAGE_DOWN);
        this.mapKey(KEY_HOME, VK_HOME);
        this.mapKey(KEY_END, VK_END);
        this.mapKey(KEY_CAPS_LOCK, VK_CAPS_LOCK);
        this.mapKey(KEY_SCROLL_LOCK, VK_SCROLL_LOCK);
        this.mapKey(KEY_NUM_LOCK, VK_NUM_LOCK);
        this.mapKey(KEY_PRINT_SCREEN, VK_PRINTSCREEN);
        this.mapKey(KEY_PAUSE, VK_PAUSE);
        this.mapKey(KEY_F1, VK_F1);
        this.mapKey(KEY_F2, VK_F2);
        this.mapKey(KEY_F3, VK_F3);
        this.mapKey(KEY_F4, VK_F4);
        this.mapKey(KEY_F5, VK_F5);
        this.mapKey(KEY_F6, VK_F6);
        this.mapKey(KEY_F7, VK_F7);
        this.mapKey(KEY_F8, VK_F8);
        this.mapKey(KEY_F9, VK_F9);
        this.mapKey(KEY_F10, VK_F10);
        this.mapKey(KEY_F11, VK_F11);
        this.mapKey(KEY_F12, VK_F12);
        this.mapKey(KEY_F13, VK_F13);
        this.mapKey(KEY_F14, VK_F14);
        this.mapKey(KEY_F15, VK_F15);
        this.mapKey(KEY_F16, VK_F16);
        this.mapKey(KEY_F17, VK_F17);
        this.mapKey(KEY_F18, VK_F18);
        this.mapKey(KEY_F19, VK_F19);
        this.mapKey(KEY_F20, VK_F20);
        this.mapKey(KEY_F21, VK_F21);
        this.mapKey(KEY_F22, VK_F22);
        this.mapKey(KEY_F23, VK_F23);
        this.mapKey(KEY_F24, VK_F24);
        this.mapKey(KEY_F25, VK_F25);
        this.mapKey(KEY_KP_0, VK_NUMPAD0, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_1, VK_NUMPAD1, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_2, VK_NUMPAD2, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_3, VK_NUMPAD3, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_4, VK_NUMPAD4, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_5, VK_NUMPAD5, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_6, VK_NUMPAD6, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_7, VK_NUMPAD7, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_8, VK_NUMPAD8, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_9, VK_NUMPAD9, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_DOT, VK_DECIMAL, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_DIV, VK_DIVIDE, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_MUL, VK_MULTIPLY, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_SUB, VK_SUBTRACT, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_ADD, VK_ADD, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_ENTER, VK_ENTER, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_KP_EQUAL, VK_EQUALS, KEY_LOCATION_NUMPAD);
        this.mapKey(KEY_LEFT_SHIFT, VK_SHIFT, KEY_LOCATION_LEFT);
        this.mapKey(KEY_LEFT_CTRL, VK_CONTROL, KEY_LOCATION_LEFT);
        this.mapKey(KEY_LEFT_ALT, VK_ALT, KEY_LOCATION_LEFT);
        this.mapKey(KEY_LEFT_SUPER, VK_WINDOWS, KEY_LOCATION_LEFT);
        this.mapKey(KEY_RIGHT_SHIFT, VK_SHIFT, KEY_LOCATION_RIGHT);
        this.mapKey(KEY_RIGHT_CTRL, VK_CONTROL, KEY_LOCATION_RIGHT);
        this.mapKey(KEY_RIGHT_ALT, VK_ALT, KEY_LOCATION_RIGHT);
        this.mapKey(KEY_RIGHT_SUPER, VK_WINDOWS, KEY_LOCATION_RIGHT);
        this.mapKey(KEY_MENU, VK_CONTEXT_MENU);
    }

    @Override
    protected void initAdapter() {
        this.mapPrintableKeys();
        this.mapMethodKeys();
    }

    @FeatureAdapter
    protected void updateKey(@NotNull KeyPressZ key,
                             @NotNull KeyMapping mapping) {
        key.pressed = keyboardListener.isPressed(mapping);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void pollDevice() {
        if (!keyboardListener.isInitialized()) {
            keyboardListener.init();
        }
    }

    @Override
    protected boolean isDeviceConnected() {
        return true; /* keyboard is always connected */
    }

}
