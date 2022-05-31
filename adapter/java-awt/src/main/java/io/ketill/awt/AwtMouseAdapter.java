package io.ketill.awt;

import io.ketill.FeatureAdapter;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.pc.CursorStateZ;
import io.ketill.pc.Mouse;
import io.ketill.pc.MouseButton;
import io.ketill.pc.MouseClickZ;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2fc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static io.ketill.pc.Mouse.*;

/**
 * A {@link Mouse} adapter using Java AWT.
 * <p>
 * <b>Note:</b> Unlike the {@code glfw-pc} module, the cursor icon will
 * always be resized depending on the settings of the operating system.
 * This means that a cursor icon with dimensions of {@code 128x128} may
 * could be resized to {@code 64x64} (or some other size).
 */
public class AwtMouseAdapter extends IoDeviceAdapter<Mouse> {

    private static final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();

    private static @Nullable Cursor invisibleCursor;

    private static @NotNull Cursor getInvisibleCursor() {
        if (invisibleCursor == null) {
            BufferedImage clearIcon = new BufferedImage(16, 16,
                    BufferedImage.TYPE_INT_ARGB);
            invisibleCursor = TOOLKIT.createCustomCursor(clearIcon,
                    new Point(0, 0), "cursor_invisible");
        }
        return invisibleCursor;
    }

    private static @Nullable Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            return null;
        }
    }

    /**
     * @param component the AWT component.
     * @return the captured mouse.
     * @throws NullPointerException if {@code component} is {@code null}.
     */
    @CapturingMethod
    public static @NotNull Mouse capture(@NotNull Component component) {
        Objects.requireNonNull(component, "component cannot be null");
        return new Mouse((d, r) -> new AwtMouseAdapter(d, r, component));
    }

    /**
     * Similar to {@link #capture(Component)}, with the key difference being
     * that the captured {@code Mouse} will be automatically polled in a
     * background thread managed by Ketill's Java AWT module.
     *
     * @param component the AWT component.
     * @return the captured mouse.
     * @throws NullPointerException if {@code component} is {@code null}.
     * @see AwtPollWorker#getDevice()
     * @see AwtPollWorker#cancel()
     */
    /* @formatter:off */
    @CapturingMethod
    public static @NotNull AwtPollWorker<Mouse>
            captureBackground(@NotNull Component component) {
        Objects.requireNonNull(component, "component cannot be null");
        Mouse mouse = capture(component);
        return AwtPollWorker.pollInBackground(mouse);
    }
    /* @formatter:on */

    private final @NotNull Component component;
    private final @NotNull AwtMouseListener mouseListener;
    private final @Nullable Robot robot;
    private boolean wasCursorVisible;
    private @Nullable Cursor currentCursor;

    /**
     * Constructs a new {@code AwtMouseAdapter}.
     *
     * @param mouse     the mouse which owns this adapter.
     * @param registry  the mouse's mapped feature registry.
     * @param component the AWT component.
     * @throws NullPointerException if {@code device}, {@code registry},
     *                              or {@code component} are {@code null}.
     */
    public AwtMouseAdapter(@NotNull Mouse mouse,
                           @NotNull MappedFeatureRegistry registry,
                           @NotNull Component component) {
        super(mouse, registry);
        this.component = Objects.requireNonNull(component,
                "component cannot be null");
        this.mouseListener = new AwtMouseListener(component);
        this.robot = createRobot();
    }

    /**
     * @param button      the mouse button to map.
     * @param mouseButton the button to map {@code button} to.
     * @throws NullPointerException if {@code button} is {@code null}.
     * @see #updateButton(MouseClickZ, int)
     */
    @MappingMethod
    protected void mapButton(@NotNull MouseButton button, int mouseButton) {
        Objects.requireNonNull(button, "button cannot be null");
        registry.mapFeature(button, mouseButton, this::updateButton);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void initAdapter() {
        this.mapButton(BUTTON_M1, 1);

        /*
         * The two lines below are not typos. For whatever reason, AWT
         * considers right click to be mouse button 3 and middle click
         * to be mouse button 2. To my knowledge, the standard for this
         * is swapped (e.g., GLFW uses mouse button 2 for right click
         * and mouse button 3 for middle click).
         */
        this.mapButton(BUTTON_M2, 3);
        this.mapButton(BUTTON_M3, 2);

        this.mapButton(BUTTON_M4, 4);
        this.mapButton(BUTTON_M5, 5);
        this.mapButton(BUTTON_M6, 6);
        this.mapButton(BUTTON_M7, 7);
        this.mapButton(BUTTON_M8, 8);

        registry.mapFeature(FEATURE_CURSOR, this::updateCursor);

        CursorStateZ cursor = registry.getInternalState(FEATURE_CURSOR);
        cursor.adapterCanSetVisible = true;
        cursor.adapterCanSetPosition = robot != null;
        cursor.adapterCanSetIcon = true;

        this.wasCursorVisible = cursor.visible;
    }

    /**
     * Updater for mouse buttons mapped via
     * {@link #mapButton(MouseButton, int)}.
     *
     * @param state the button state.
     * @param id    the button ID.
     */
    @FeatureAdapter
    protected void updateButton(@NotNull MouseClickZ state, int id) {
        state.pressed = mouseListener.isPressed(id);
    }

    private void updateCursorPos(@NotNull CursorStateZ state) {
        if (GraphicsEnvironment.isHeadless() || !component.isVisible()) {
            state.currentPos.set(0, 0);
            return; /* cursor position not available */
        }

        Vector2fc requested = state.requestedPos;
        state.requestedPos = null;
        if (requested != null && robot != null) {
            state.currentPos.set(requested);
            robot.mouseMove((int) requested.x(), (int) requested.y());
            return; /* position already set */
        }

        /*
         * It's common for isVisible() to return false just too late before
         * calling getLocationOnScreen(), causing an exception to be thrown
         * here. As such, just ignore this exception and return.
         */
        Point componentLoc;
        try {
            componentLoc = component.getLocationOnScreen();
        } catch (IllegalComponentStateException e) {
            return; /* cannot continue from here */
        }

        /*
         * MouseInfo returns the location of the mouse cursor relative to
         * the screen. We want to return the coordinates relative to the
         * component being used to listen for mouse input.
         */
        Point cursorLoc = MouseInfo.getPointerInfo().getLocation();
        double relativeX = cursorLoc.getX() - componentLoc.getX();
        double relativeY = cursorLoc.getY() - componentLoc.getY();

        state.currentPos.x = (float) relativeX;
        state.currentPos.y = (float) relativeY;
    }

    private void updateCursorVisibility(@NotNull CursorStateZ state) {
        if (GraphicsEnvironment.isHeadless()) {
            return; /* cannot update visibility */
        }

        if (!wasCursorVisible && state.visible) {
            component.setCursor(currentCursor);
            this.wasCursorVisible = true;
        } else if (wasCursorVisible && !state.visible) {
            component.setCursor(getInvisibleCursor());
            this.wasCursorVisible = false;
        }
    }

    private void updateCursorIcon(@NotNull CursorStateZ state) {
        if (GraphicsEnvironment.isHeadless()) {
            return; /* cannot create cursor icons */
        } else if (!state.updatedIcon) {
            return; /* icon has not been updated */
        }

        /*
         * If the icon for the cursor is null, it indicates the default
         * icon should be used. In Java AWT, null represents the default
         * cursor. Attempting to create a cursor with a null icon would
         * also likely result in a NullPointerException being thrown.
         */
        if (state.icon == null) {
            this.currentCursor = null;
        } else {
            this.currentCursor = TOOLKIT.createCustomCursor(state.icon,
                    component.getLocation(), "cursor_custom");
        }

        /*
         * If the cursor is currently visible, it should be updated here.
         * If it is currently invisible, the component's cursor will be
         * updated when the user makes the cursor visible again.
         */
        if (state.visible) {
            component.setCursor(currentCursor);
        }

        state.updatedIcon = false;
    }

    /**
     * Updater for {@link Mouse#FEATURE_CURSOR}.
     *
     * @param state the cursor state.
     */
    @FeatureAdapter
    protected void updateCursor(@NotNull CursorStateZ state) {
        this.updateCursorPos(state);
        this.updateCursorVisibility(state);
        this.updateCursorIcon(state);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void pollDevice() {
        if (!mouseListener.isInitialized()) {
            mouseListener.init();
        }
    }

    @Override
    protected final boolean isDeviceConnected() {
        return true; /* mouse is always connected */
    }

}
