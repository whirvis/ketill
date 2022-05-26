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
 */
public class AwtMouseAdapter extends IoDeviceAdapter<Mouse> {

    /* @formatter:off */
    private static final @NotNull Cursor INVISIBLE_CURSOR =
            Toolkit.getDefaultToolkit().createCustomCursor(
                    new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB),
                    new Point(0, 0), "invisible_cursor");
    /* @formatter:on */

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

    private static @Nullable Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            return null;
        }
    }

    private final @NotNull Component component;
    private final @NotNull AwtMouseListener mouseListener;
    private final @Nullable Robot robot;
    private boolean wasCursorVisible;

    /**
     * @param device    the device which owns this adapter.
     * @param registry  the device's mapped feature registry.
     * @param component the AWT component.
     * @throws NullPointerException if {@code device}, {@code registry},
     *                              or {@code component} are {@code null}.
     */
    public AwtMouseAdapter(@NotNull Mouse device,
                           @NotNull MappedFeatureRegistry registry,
                           @NotNull Component component) {
        super(device, registry);
        this.component = Objects.requireNonNull(component,
                "component cannot be null");
        this.mouseListener = new AwtMouseListener(component);
        this.robot = createRobot();
    }

    /**
     * @param button      the mouse button to map.
     * @param mouseButton the button to map {@code button} to.
     * @throws NullPointerException     if {@code button} is {@code null}.
     * @throws IllegalArgumentException if {@code mouseButton} is negative.
     * @see #updateButton(MouseClickZ, int)
     */
    @MappingMethod
    protected void mapButton(@NotNull MouseButton button, int mouseButton) {
        Objects.requireNonNull(button, "button");
        if (mouseButton < 0) {
            throw new IllegalArgumentException("mouseButton < 0");
        }
        registry.mapFeature(button, mouseButton, this::updateButton);
    }

    @Override
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

        this.wasCursorVisible = cursor.visible;
    }

    @FeatureAdapter
    protected void updateButton(@NotNull MouseClickZ click, int id) {
        click.pressed = mouseListener.isPressed(id);
    }

    @FeatureAdapter
    protected void updateCursor(@NotNull CursorStateZ cursor) {
        Vector2fc requested = cursor.requestedPos;
        cursor.requestedPos = null;
        if (requested != null && robot != null) {
            cursor.currentPos.set(requested);
            robot.mouseMove((int) requested.x(), (int) requested.y());
        } else {
            Point cursorLoc = MouseInfo.getPointerInfo().getLocation();
            Point componentLoc = component.getLocationOnScreen();

            /*
             * MouseInfo returns the location of the mouse cursor relative
             * to the screen. We want to return the coordinates relative to
             * the component being used to listen for mouse input.
             */
            double relativeX = cursorLoc.getX() - componentLoc.getX();
            double relativeY = cursorLoc.getY() - componentLoc.getY();

            cursor.currentPos.x = (float) relativeX;
            cursor.currentPos.y = (float) relativeY;
        }

        if (!wasCursorVisible && cursor.visible) {
            component.setCursor(null);
            this.wasCursorVisible = true;
        } else if (wasCursorVisible && !cursor.visible) {
            component.setCursor(INVISIBLE_CURSOR);
            this.wasCursorVisible = false;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void pollDevice() {
        if (!mouseListener.isInitialized()) {
            mouseListener.init();
        }
    }

    @Override
    protected boolean isDeviceConnected() {
        return true; /* mouse is always connected */
    }

}
