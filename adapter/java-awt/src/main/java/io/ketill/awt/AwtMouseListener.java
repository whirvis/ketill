package io.ketill.awt;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

final class AwtMouseListener implements MouseListener {

    /*
     * In the HID protocol specification, 3 bits are used for mouse buttons.
     * This means that there can be, in total, twelve buttons on a mouse.
     * However, just to be safe, double this amount is assumed to exist.
     */
    static final int MOUSE_BUTTON_COUNT = 24;

    private final Component component;
    private final boolean[] buttons;
    private boolean initialized;

    AwtMouseListener(@NotNull Component component) {
        this.component = component;
        this.buttons = new boolean[MOUSE_BUTTON_COUNT];
    }

    boolean isPressed(int id) {
        return this.buttons[id];
    }

    boolean isInitialized() {
        return this.initialized;
    }

    void init() {
        if (this.isInitialized()) {
            throw new IllegalStateException("already initialized");
        }
        component.addMouseListener(this);
        this.initialized = true;
    }

    @Override
    public void mouseClicked(@NotNull MouseEvent event) {
        /* ignore this event */
    }

    @Override
    public void mousePressed(@NotNull MouseEvent event) {
        this.buttons[event.getButton()] = true;
    }

    @Override
    public void mouseReleased(@NotNull MouseEvent event) {
        this.buttons[event.getButton()] = false;
    }

    @Override
    public void mouseEntered(@NotNull MouseEvent event) {
        /* ignore this event */
    }

    @Override
    public void mouseExited(@NotNull MouseEvent event) {
        /* ignore this event */
    }

}
