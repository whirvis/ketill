package io.ketill.glfw.pc;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.glfw.GlfwDeviceAdapter;
import io.ketill.glfw.WranglerMethod;
import io.ketill.pc.KeyPressZ;
import io.ketill.pc.Keyboard;
import io.ketill.pc.KeyboardKey;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.ketill.pc.Keyboard.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * A {@link Keyboard} adapter using GLFW.
 */
public class GlfwKeyboardAdapter extends GlfwDeviceAdapter<Keyboard> {

    /**
     * @param ptr_glfwWindow the GLFW window pointer.
     * @return the wrangled keyboard.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero.)
     */
    @WranglerMethod
    public static @NotNull Keyboard wrangle(long ptr_glfwWindow) {
        return new Keyboard((d, r) -> new GlfwKeyboardAdapter(d, r,
                ptr_glfwWindow));
    }

    /**
     * Constructs a new {@code GlfwKeyboardAdapter}.
     *
     * @param keyboard       the keyboard which owns this adapter.
     * @param registry       the keyboard's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code keyboard} or
     *                              {@code registry} are {@code null};
     *                              if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero.)
     */
    public GlfwKeyboardAdapter(@NotNull Keyboard keyboard,
                               @NotNull MappedFeatureRegistry registry,
                               long ptr_glfwWindow) {
        super(keyboard, registry, ptr_glfwWindow);
    }

    /**
     * @param key     the keyboard key to map.
     * @param glfwKey the GLFW key to map {@code key} to.
     * @throws NullPointerException     if {@code key} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwKey} is negative.
     * @see #updateKey(KeyPressZ, int)
     */
    @MappingMethod
    protected void mapKey(@NotNull KeyboardKey key, int glfwKey) {
        Objects.requireNonNull(key, "key");
        if (glfwKey < 0) {
            throw new IllegalArgumentException("glfwKey < 0");
        }
        registry.mapFeature(key, glfwKey, this::updateKey);
    }

    private void mapPrintableKeys() {
        this.mapKey(KEY_SPACE, GLFW_KEY_SPACE);
        this.mapKey(KEY_APOSTROPHE, GLFW_KEY_APOSTROPHE);
        this.mapKey(KEY_COMMA, GLFW_KEY_COMMA);
        this.mapKey(KEY_MINUS, GLFW_KEY_MINUS);
        this.mapKey(KEY_PERIOD, GLFW_KEY_PERIOD);
        this.mapKey(KEY_SLASH, GLFW_KEY_SLASH);
        this.mapKey(KEY_ZERO, GLFW_KEY_0);
        this.mapKey(KEY_ONE, GLFW_KEY_1);
        this.mapKey(KEY_TWO, GLFW_KEY_2);
        this.mapKey(KEY_THREE, GLFW_KEY_3);
        this.mapKey(KEY_FOUR, GLFW_KEY_4);
        this.mapKey(KEY_FIVE, GLFW_KEY_5);
        this.mapKey(KEY_SIX, GLFW_KEY_6);
        this.mapKey(KEY_SEVEN, GLFW_KEY_7);
        this.mapKey(KEY_EIGHT, GLFW_KEY_8);
        this.mapKey(KEY_NINE, GLFW_KEY_9);
        this.mapKey(KEY_SEMICOLON, GLFW_KEY_SEMICOLON);
        this.mapKey(KEY_EQUAL, GLFW_KEY_EQUAL);
        this.mapKey(KEY_A, GLFW_KEY_A);
        this.mapKey(KEY_B, GLFW_KEY_B);
        this.mapKey(KEY_C, GLFW_KEY_C);
        this.mapKey(KEY_D, GLFW_KEY_D);
        this.mapKey(KEY_E, GLFW_KEY_E);
        this.mapKey(KEY_F, GLFW_KEY_F);
        this.mapKey(KEY_G, GLFW_KEY_G);
        this.mapKey(KEY_H, GLFW_KEY_H);
        this.mapKey(KEY_I, GLFW_KEY_I);
        this.mapKey(KEY_J, GLFW_KEY_J);
        this.mapKey(KEY_K, GLFW_KEY_K);
        this.mapKey(KEY_L, GLFW_KEY_L);
        this.mapKey(KEY_M, GLFW_KEY_M);
        this.mapKey(KEY_N, GLFW_KEY_N);
        this.mapKey(KEY_O, GLFW_KEY_O);
        this.mapKey(KEY_P, GLFW_KEY_P);
        this.mapKey(KEY_Q, GLFW_KEY_Q);
        this.mapKey(KEY_R, GLFW_KEY_R);
        this.mapKey(KEY_S, GLFW_KEY_S);
        this.mapKey(KEY_T, GLFW_KEY_T);
        this.mapKey(KEY_U, GLFW_KEY_U);
        this.mapKey(KEY_V, GLFW_KEY_V);
        this.mapKey(KEY_W, GLFW_KEY_W);
        this.mapKey(KEY_X, GLFW_KEY_X);
        this.mapKey(KEY_Y, GLFW_KEY_Y);
        this.mapKey(KEY_Z, GLFW_KEY_Z);
        this.mapKey(KEY_LEFT_BRACKET, GLFW_KEY_LEFT_BRACKET);
        this.mapKey(KEY_BACKSLASH, GLFW_KEY_BACKSLASH);
        this.mapKey(KEY_RIGHT_BRACKET, GLFW_KEY_RIGHT_BRACKET);
        this.mapKey(KEY_GRAVE_ACCENT, GLFW_KEY_GRAVE_ACCENT);
        this.mapKey(KEY_WORLD_1, GLFW_KEY_WORLD_1);
        this.mapKey(KEY_WORLD_2, GLFW_KEY_WORLD_2);
    }

    private void mapMethodKeys() {
        this.mapKey(KEY_ESCAPE, GLFW_KEY_ESCAPE);
        this.mapKey(KEY_ENTER, GLFW_KEY_ENTER);
        this.mapKey(KEY_TAB, GLFW_KEY_TAB);
        this.mapKey(KEY_BACKSPACE, GLFW_KEY_BACKSPACE);
        this.mapKey(KEY_INSERT, GLFW_KEY_INSERT);
        this.mapKey(KEY_DELETE, GLFW_KEY_DELETE);
        this.mapKey(KEY_RIGHT, GLFW_KEY_RIGHT);
        this.mapKey(KEY_LEFT, GLFW_KEY_LEFT);
        this.mapKey(KEY_DOWN, GLFW_KEY_DOWN);
        this.mapKey(KEY_UP, GLFW_KEY_UP);
        this.mapKey(KEY_PAGE_UP, GLFW_KEY_PAGE_UP);
        this.mapKey(KEY_PAGE_DOWN, GLFW_KEY_PAGE_DOWN);
        this.mapKey(KEY_HOME, GLFW_KEY_HOME);
        this.mapKey(KEY_END, GLFW_KEY_END);
        this.mapKey(KEY_CAPS_LOCK, GLFW_KEY_CAPS_LOCK);
        this.mapKey(KEY_SCROLL_LOCK, GLFW_KEY_SCROLL_LOCK);
        this.mapKey(KEY_NUM_LOCK, GLFW_KEY_NUM_LOCK);
        this.mapKey(KEY_PRINT_SCREEN, GLFW_KEY_PRINT_SCREEN);
        this.mapKey(KEY_PAUSE, GLFW_KEY_PAUSE);
        this.mapKey(KEY_F1, GLFW_KEY_F1);
        this.mapKey(KEY_F2, GLFW_KEY_F2);
        this.mapKey(KEY_F3, GLFW_KEY_F3);
        this.mapKey(KEY_F4, GLFW_KEY_F4);
        this.mapKey(KEY_F5, GLFW_KEY_F5);
        this.mapKey(KEY_F6, GLFW_KEY_F6);
        this.mapKey(KEY_F7, GLFW_KEY_F7);
        this.mapKey(KEY_F8, GLFW_KEY_F8);
        this.mapKey(KEY_F9, GLFW_KEY_F9);
        this.mapKey(KEY_F10, GLFW_KEY_F10);
        this.mapKey(KEY_F11, GLFW_KEY_F11);
        this.mapKey(KEY_F12, GLFW_KEY_F12);
        this.mapKey(KEY_F13, GLFW_KEY_F13);
        this.mapKey(KEY_F14, GLFW_KEY_F14);
        this.mapKey(KEY_F15, GLFW_KEY_F15);
        this.mapKey(KEY_F16, GLFW_KEY_F16);
        this.mapKey(KEY_F17, GLFW_KEY_F17);
        this.mapKey(KEY_F18, GLFW_KEY_F18);
        this.mapKey(KEY_F19, GLFW_KEY_F19);
        this.mapKey(KEY_F20, GLFW_KEY_F20);
        this.mapKey(KEY_F21, GLFW_KEY_F21);
        this.mapKey(KEY_F22, GLFW_KEY_F22);
        this.mapKey(KEY_F23, GLFW_KEY_F23);
        this.mapKey(KEY_F24, GLFW_KEY_F24);
        this.mapKey(KEY_F25, GLFW_KEY_F25);
        this.mapKey(KEY_KP_0, GLFW_KEY_KP_0);
        this.mapKey(KEY_KP_1, GLFW_KEY_KP_1);
        this.mapKey(KEY_KP_2, GLFW_KEY_KP_2);
        this.mapKey(KEY_KP_3, GLFW_KEY_KP_3);
        this.mapKey(KEY_KP_4, GLFW_KEY_KP_4);
        this.mapKey(KEY_KP_5, GLFW_KEY_KP_5);
        this.mapKey(KEY_KP_6, GLFW_KEY_KP_6);
        this.mapKey(KEY_KP_7, GLFW_KEY_KP_7);
        this.mapKey(KEY_KP_8, GLFW_KEY_KP_8);
        this.mapKey(KEY_KP_9, GLFW_KEY_KP_9);
        this.mapKey(KEY_KP_DOT, GLFW_KEY_KP_DECIMAL);
        this.mapKey(KEY_KP_DIV, GLFW_KEY_KP_DIVIDE);
        this.mapKey(KEY_KP_MUL, GLFW_KEY_KP_MULTIPLY);
        this.mapKey(KEY_KP_SUB, GLFW_KEY_KP_SUBTRACT);
        this.mapKey(KEY_KP_ADD, GLFW_KEY_KP_ADD);
        this.mapKey(KEY_KP_ENTER, GLFW_KEY_KP_ENTER);
        this.mapKey(KEY_KP_EQUAL, GLFW_KEY_KP_EQUAL);
        this.mapKey(KEY_LEFT_SHIFT, GLFW_KEY_LEFT_SHIFT);
        this.mapKey(KEY_LEFT_CTRL, GLFW_KEY_LEFT_CONTROL);
        this.mapKey(KEY_LEFT_ALT, GLFW_KEY_LEFT_ALT);
        this.mapKey(KEY_LEFT_SUPER, GLFW_KEY_LEFT_SUPER);
        this.mapKey(KEY_RIGHT_SHIFT, GLFW_KEY_RIGHT_SHIFT);
        this.mapKey(KEY_RIGHT_CTRL, GLFW_KEY_RIGHT_CONTROL);
        this.mapKey(KEY_RIGHT_ALT, GLFW_KEY_RIGHT_ALT);
        this.mapKey(KEY_RIGHT_SUPER, GLFW_KEY_RIGHT_SUPER);
        this.mapKey(KEY_MENU, GLFW_KEY_MENU);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void initAdapter() {
        this.mapPrintableKeys();
        this.mapMethodKeys();
    }

    @FeatureAdapter
    protected void updateKey(@NotNull KeyPressZ key, int glfwKey) {
        int status = glfwGetKey(ptr_glfwWindow, glfwKey);
        key.pressed = status >= GLFW_PRESS;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void pollDevice() {
        /*
         * Although there is currently nothing to poll here, annotate this
         * method with @MustBeInvokedByOverriders in case there is something
         * to poll in a later version.
         */
    }

    @Override
    protected final boolean isDeviceConnected() {
        return true; /* keyboard is always connected */
    }

}
