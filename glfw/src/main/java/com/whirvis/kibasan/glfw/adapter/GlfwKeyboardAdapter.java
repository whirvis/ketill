package com.whirvis.kibasan.glfw.adapter;

import com.whirvis.kibasan.Button1b;
import com.whirvis.kibasan.FeatureAdapter;
import com.whirvis.kibasan.MappedFeatureRegistry;
import com.whirvis.kibasan.pc.Keyboard;
import com.whirvis.kibasan.pc.KeyboardKey;
import org.jetbrains.annotations.NotNull;

import static com.whirvis.kibasan.pc.Keyboard.*;
import static org.lwjgl.glfw.GLFW.*;

public class GlfwKeyboardAdapter extends GlfwDeviceAdapter<Keyboard> {

    public GlfwKeyboardAdapter(long ptr_glfwWindow) {
        super(ptr_glfwWindow);
    }

    protected void mapKey(@NotNull MappedFeatureRegistry registry,
                          @NotNull KeyboardKey key, int glfwKey) {
        registry.mapFeature(key, glfwKey, this::updateButton);
    }

    private void mapPrintableKeys(@NotNull MappedFeatureRegistry registry) {
        this.mapKey(registry, KEY_SPACE, GLFW_KEY_SPACE);
        this.mapKey(registry, KEY_APOSTROPHE, GLFW_KEY_APOSTROPHE);
        this.mapKey(registry, KEY_COMMA, GLFW_KEY_COMMA);
        this.mapKey(registry, KEY_MINUS, GLFW_KEY_MINUS);
        this.mapKey(registry, KEY_PERIOD, GLFW_KEY_PERIOD);
        this.mapKey(registry, KEY_SLASH, GLFW_KEY_SLASH);
        this.mapKey(registry, KEY_ZERO, GLFW_KEY_0);
        this.mapKey(registry, KEY_ONE, GLFW_KEY_1);
        this.mapKey(registry, KEY_TWO, GLFW_KEY_2);
        this.mapKey(registry, KEY_THREE, GLFW_KEY_3);
        this.mapKey(registry, KEY_FOUR, GLFW_KEY_4);
        this.mapKey(registry, KEY_FIVE, GLFW_KEY_5);
        this.mapKey(registry, KEY_SIX, GLFW_KEY_6);
        this.mapKey(registry, KEY_SEVEN, GLFW_KEY_7);
        this.mapKey(registry, KEY_EIGHT, GLFW_KEY_8);
        this.mapKey(registry, KEY_NINE, GLFW_KEY_9);
        this.mapKey(registry, KEY_SEMICOLON, GLFW_KEY_SEMICOLON);
        this.mapKey(registry, KEY_EQUAL, GLFW_KEY_EQUAL);
        this.mapKey(registry, KEY_A, GLFW_KEY_A);
        this.mapKey(registry, KEY_B, GLFW_KEY_B);
        this.mapKey(registry, KEY_C, GLFW_KEY_C);
        this.mapKey(registry, KEY_D, GLFW_KEY_D);
        this.mapKey(registry, KEY_E, GLFW_KEY_E);
        this.mapKey(registry, KEY_F, GLFW_KEY_F);
        this.mapKey(registry, KEY_G, GLFW_KEY_G);
        this.mapKey(registry, KEY_H, GLFW_KEY_H);
        this.mapKey(registry, KEY_I, GLFW_KEY_I);
        this.mapKey(registry, KEY_J, GLFW_KEY_J);
        this.mapKey(registry, KEY_K, GLFW_KEY_K);
        this.mapKey(registry, KEY_L, GLFW_KEY_L);
        this.mapKey(registry, KEY_M, GLFW_KEY_M);
        this.mapKey(registry, KEY_N, GLFW_KEY_N);
        this.mapKey(registry, KEY_O, GLFW_KEY_O);
        this.mapKey(registry, KEY_P, GLFW_KEY_P);
        this.mapKey(registry, KEY_Q, GLFW_KEY_Q);
        this.mapKey(registry, KEY_R, GLFW_KEY_R);
        this.mapKey(registry, KEY_S, GLFW_KEY_S);
        this.mapKey(registry, KEY_T, GLFW_KEY_T);
        this.mapKey(registry, KEY_U, GLFW_KEY_U);
        this.mapKey(registry, KEY_V, GLFW_KEY_V);
        this.mapKey(registry, KEY_W, GLFW_KEY_W);
        this.mapKey(registry, KEY_X, GLFW_KEY_X);
        this.mapKey(registry, KEY_Y, GLFW_KEY_Y);
        this.mapKey(registry, KEY_Z, GLFW_KEY_Z);
        this.mapKey(registry, KEY_LEFT_BRACKET, GLFW_KEY_LEFT_BRACKET);
        this.mapKey(registry, KEY_BACKSLASH, GLFW_KEY_BACKSLASH);
        this.mapKey(registry, KEY_RIGHT_BRACKET, GLFW_KEY_RIGHT_BRACKET);
        this.mapKey(registry, KEY_GRAVE_ACCENT, GLFW_KEY_GRAVE_ACCENT);
        this.mapKey(registry, KEY_WORLD_1, GLFW_KEY_WORLD_1);
        this.mapKey(registry, KEY_WORLD_2, GLFW_KEY_WORLD_2);
    }

    private void mapMethodKeys(@NotNull MappedFeatureRegistry registry) {
        this.mapKey(registry, KEY_ESCAPE, GLFW_KEY_ESCAPE);
        this.mapKey(registry, KEY_ENTER, GLFW_KEY_ENTER);
        this.mapKey(registry, KEY_TAB, GLFW_KEY_TAB);
        this.mapKey(registry, KEY_BACKSPACE, GLFW_KEY_BACKSPACE);
        this.mapKey(registry, KEY_INSERT, GLFW_KEY_INSERT);
        this.mapKey(registry, KEY_DELETE, GLFW_KEY_DELETE);
        this.mapKey(registry, KEY_RIGHT, GLFW_KEY_RIGHT);
        this.mapKey(registry, KEY_LEFT, GLFW_KEY_LEFT);
        this.mapKey(registry, KEY_DOWN, GLFW_KEY_DOWN);
        this.mapKey(registry, KEY_UP, GLFW_KEY_UP);
        this.mapKey(registry, KEY_PAGE_UP, GLFW_KEY_PAGE_UP);
        this.mapKey(registry, KEY_PAGE_DOWN, GLFW_KEY_PAGE_DOWN);
        this.mapKey(registry, KEY_HOME, GLFW_KEY_HOME);
        this.mapKey(registry, KEY_END, GLFW_KEY_END);
        this.mapKey(registry, KEY_CAPS_LOCK, GLFW_KEY_CAPS_LOCK);
        this.mapKey(registry, KEY_SCROLL_LOCK, GLFW_KEY_SCROLL_LOCK);
        this.mapKey(registry, KEY_NUM_LOCK, GLFW_KEY_NUM_LOCK);
        this.mapKey(registry, KEY_PRINT_SCREEN, GLFW_KEY_PRINT_SCREEN);
        this.mapKey(registry, KEY_PAUSE, GLFW_KEY_PAUSE);
        this.mapKey(registry, KEY_F1, GLFW_KEY_F1);
        this.mapKey(registry, KEY_F2, GLFW_KEY_F2);
        this.mapKey(registry, KEY_F3, GLFW_KEY_F3);
        this.mapKey(registry, KEY_F4, GLFW_KEY_F4);
        this.mapKey(registry, KEY_F5, GLFW_KEY_F5);
        this.mapKey(registry, KEY_F6, GLFW_KEY_F6);
        this.mapKey(registry, KEY_F7, GLFW_KEY_F7);
        this.mapKey(registry, KEY_F8, GLFW_KEY_F8);
        this.mapKey(registry, KEY_F9, GLFW_KEY_F9);
        this.mapKey(registry, KEY_F10, GLFW_KEY_F10);
        this.mapKey(registry, KEY_F11, GLFW_KEY_F11);
        this.mapKey(registry, KEY_F12, GLFW_KEY_F12);
        this.mapKey(registry, KEY_F13, GLFW_KEY_F13);
        this.mapKey(registry, KEY_F14, GLFW_KEY_F14);
        this.mapKey(registry, KEY_F15, GLFW_KEY_F15);
        this.mapKey(registry, KEY_F16, GLFW_KEY_F16);
        this.mapKey(registry, KEY_F17, GLFW_KEY_F17);
        this.mapKey(registry, KEY_F18, GLFW_KEY_F18);
        this.mapKey(registry, KEY_F19, GLFW_KEY_F19);
        this.mapKey(registry, KEY_F20, GLFW_KEY_F20);
        this.mapKey(registry, KEY_F21, GLFW_KEY_F21);
        this.mapKey(registry, KEY_F22, GLFW_KEY_F22);
        this.mapKey(registry, KEY_F23, GLFW_KEY_F23);
        this.mapKey(registry, KEY_F24, GLFW_KEY_F24);
        this.mapKey(registry, KEY_F25, GLFW_KEY_F25);
        this.mapKey(registry, KEY_KP_0, GLFW_KEY_KP_0);
        this.mapKey(registry, KEY_KP_1, GLFW_KEY_KP_1);
        this.mapKey(registry, KEY_KP_2, GLFW_KEY_KP_2);
        this.mapKey(registry, KEY_KP_3, GLFW_KEY_KP_3);
        this.mapKey(registry, KEY_KP_4, GLFW_KEY_KP_4);
        this.mapKey(registry, KEY_KP_5, GLFW_KEY_KP_5);
        this.mapKey(registry, KEY_KP_6, GLFW_KEY_KP_6);
        this.mapKey(registry, KEY_KP_7, GLFW_KEY_KP_7);
        this.mapKey(registry, KEY_KP_8, GLFW_KEY_KP_8);
        this.mapKey(registry, KEY_KP_9, GLFW_KEY_KP_9);
        this.mapKey(registry, KEY_KP_DOT, GLFW_KEY_KP_DECIMAL);
        this.mapKey(registry, KEY_KP_DIV, GLFW_KEY_KP_DIVIDE);
        this.mapKey(registry, KEY_KP_MUL, GLFW_KEY_KP_MULTIPLY);
        this.mapKey(registry, KEY_KP_SUB, GLFW_KEY_KP_SUBTRACT);
        this.mapKey(registry, KEY_KP_ADD, GLFW_KEY_KP_ADD);
        this.mapKey(registry, KEY_KP_ENTER, GLFW_KEY_KP_ENTER);
        this.mapKey(registry, KEY_KP_EQUAL, GLFW_KEY_KP_EQUAL);
        this.mapKey(registry, KEY_LEFT_SHIFT, GLFW_KEY_LEFT_SHIFT);
        this.mapKey(registry, KEY_LEFT_CTRL, GLFW_KEY_LEFT_CONTROL);
        this.mapKey(registry, KEY_LEFT_ALT, GLFW_KEY_LEFT_ALT);
        this.mapKey(registry, KEY_LEFT_SUPER, GLFW_KEY_LEFT_SUPER);
        this.mapKey(registry, KEY_RIGHT_SHIFT, GLFW_KEY_RIGHT_SHIFT);
        this.mapKey(registry, KEY_RIGHT_CTRL, GLFW_KEY_RIGHT_CONTROL);
        this.mapKey(registry, KEY_RIGHT_ALT, GLFW_KEY_RIGHT_ALT);
        this.mapKey(registry, KEY_RIGHT_SUPER, GLFW_KEY_RIGHT_SUPER);
        this.mapKey(registry, KEY_MENU, GLFW_KEY_MENU);
    }

    @Override
    protected void initAdapter(@NotNull Keyboard keyboard,
                               @NotNull MappedFeatureRegistry registry) {
        this.mapPrintableKeys(registry);
        this.mapMethodKeys(registry);
    }

    @FeatureAdapter
    protected void updateButton(@NotNull Button1b button, int glfwKey) {
        int status = glfwGetKey(ptr_glfwWindow, glfwKey);
        button.pressed = status >= GLFW_PRESS;
    }

    @Override
    protected void pollDevice(@NotNull Keyboard keyboard) {
        /* nothing to poll */
    }

    @Override
    protected boolean isDeviceConnected(@NotNull Keyboard keyboard) {
        return true; /* keyboard is always connected */
    }

}
