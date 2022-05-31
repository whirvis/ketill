package io.ketill.awt;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map;

final class AwtKeyboardListener implements KeyListener {

    private final Component component;
    private final Map<Integer, Map<Integer, KeyState>> keys;
    private boolean initialized;

    AwtKeyboardListener(@NotNull Component component) {
        this.component = component;
        this.keys = new HashMap<>();
    }

    private @NotNull KeyState getState(int keyCode, int keyLocation) {
        if (!keys.containsKey(keyCode)) {
            keys.put(keyCode, new HashMap<>());
        }

        Map<Integer, KeyState> locations = keys.get(keyCode);
        if (!locations.containsKey(keyLocation)) {
            locations.put(keyLocation, new KeyState());
        }

        return locations.get(keyLocation);
    }

    private @NotNull KeyState getState(@NotNull KeyEvent event) {
        int keyCode = event.getKeyCode();
        int keyLocation = event.getKeyLocation();
        return this.getState(keyCode, keyLocation);
    }

    boolean isPressed(int keyCode, int keyLocation) {
        return this.getState(keyCode, keyLocation).pressed;
    }

    boolean isPressed(@NotNull AwtKeyMapping mapping) {
        return this.isPressed(mapping.keyCode, mapping.keyLocation);
    }

    boolean isInitialized() {
        return this.initialized;
    }

    void init() {
        if (this.isInitialized()) {
            throw new IllegalStateException("already initialized");
        }
        component.addKeyListener(this);
        this.initialized = true;
    }

    @Override
    public void keyTyped(@NotNull KeyEvent event) {
        /* ignore this event */
    }

    @Override
    public void keyPressed(@NotNull KeyEvent event) {
        KeyState state = this.getState(event);
        state.pressed = true;
    }

    @Override
    public void keyReleased(@NotNull KeyEvent event) {
        KeyState state = this.getState(event);
        state.pressed = false;
    }

}
