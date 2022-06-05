package io.ketill.glfw.pc;

import io.ketill.FeatureAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.MappingMethod;
import io.ketill.glfw.GlfwDeviceAdapter;
import io.ketill.glfw.GlfwUtils;
import io.ketill.glfw.WranglerMethod;
import io.ketill.pc.CursorStateZ;
import io.ketill.pc.Mouse;
import io.ketill.pc.MouseButton;
import io.ketill.pc.MouseClickZ;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Objects;

import static io.ketill.pc.Mouse.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * A {@link Mouse} adapter using GLFW.
 *
 * @see #mapButton(MouseButton, int)
 */
public class GlfwMouseAdapter extends GlfwDeviceAdapter<Mouse> {

    /**
     * Wrangles the {@link Mouse} from a GLFW window.
     * <p>
     * <b>Thread safety:</b> This method is <i>not</i> thread-safe. It must
     * be called on the thread which created {@code ptr_glfwWindow}.
     *
     * @param ptr_glfwWindow the GLFW window pointer.
     * @return the wrangled mouse.
     * @throws NullPointerException if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     */
    @WranglerMethod
    public static @NotNull Mouse wrangle(long ptr_glfwWindow) {
        return new Mouse((d, r) -> new GlfwMouseAdapter(d, r, ptr_glfwWindow));
    }

    /**
     * Creates a GLFW image from an existing Java AWT image.
     * <p>
     * <b>Note:</b> This image is allocated in memory outside the scope of
     * Java's garbage collector. As such, it is the responsible caller to
     * free the returned image when they are done with it. This can be done
     * with {@link GLFWImage#free()}.
     *
     * @param img the image to convert.
     * @return the initialized GLFW image.
     * @throws NullPointerException if {@code img} is {@code null}.
     */
    protected static @NotNull GLFWImage createGlfwImage(@NotNull Image img) {
        Objects.requireNonNull(img, "img cannot be null");

        int width = img.getWidth(null);
        int height = img.getHeight(null);

        /*
         * To make conversion easier, convert the parameter to an instance
         * of BufferedImage. This will allow us to grab the color of each
         * pixel directly. If the parameter is already a buffered image,
         * then it can simply be cast back to avoid unnecessary rendering.
         * Otherwise, it will be rendered to a temporary buffered image.
         */
        BufferedImage buffered;
        if (img instanceof BufferedImage) {
            buffered = (BufferedImage) img;
        } else {
            buffered = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphics = buffered.createGraphics();
            graphics.drawImage(img, 0, 0, null);
            graphics.dispose();
        }

        int size = width * height * 4;
        ByteBuffer pixels = BufferUtils.createByteBuffer(size);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                /*
                 * Java AWT returns pixels in ARGB format, but GLFW expects
                 * for them to be RGBA format. The code below rearranges the
                 * bytes of the pixel color accordingly.
                 */
                int color = buffered.getRGB(x, y);
                pixels.put((byte) ((color >> 16) & 0xFF));
                pixels.put((byte) ((color >> 8) & 0xFF));
                pixels.put((byte) (color & 0xFF));
                pixels.put((byte) ((color >> 24) & 0xFF));
            }
        }
        pixels.flip();

        GLFWImage glfwImg = GLFWImage.calloc();
        glfwImg.set(width, height, pixels);
        return glfwImg;
    }

    private final double[] xPos;
    private final double[] yPos;
    private boolean wasCursorVisible;
    private GLFWImage glfwCursorImage;
    private long ptr_glfwCursor;

    /**
     * Constructs a new {@code GlfwMouseAdapter}.
     *
     * @param mouse          the mouse which owns this adapter.
     * @param registry       the mouse's mapped feature registry.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code mouse} or
     *                              {@code registry} are {@code null};
     *                              if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     */
    public GlfwMouseAdapter(@NotNull Mouse mouse,
                            @NotNull MappedFeatureRegistry registry,
                            long ptr_glfwWindow) {
        super(mouse, registry, ptr_glfwWindow);
        this.xPos = new double[1];
        this.yPos = new double[1];
    }

    /**
     * Maps a {@link MouseButton} to a GLFW button.
     * <p>
     * <b>Thread safety:</b> This method is <i>thread-safe.</i>
     * No calls to the GLFW library are made.
     *
     * @param button     the mouse button to map.
     * @param glfwButton the GLFW button to map {@code button} to.
     * @throws NullPointerException     if {@code button} is {@code null}.
     * @throws IllegalArgumentException if {@code glfwButton} is negative.
     * @see #updateButton(MouseClickZ, int)
     */
    @MappingMethod
    protected void mapButton(@NotNull MouseButton button, int glfwButton) {
        Objects.requireNonNull(button, "button cannot be null");
        GlfwUtils.requireButton(glfwButton, "glfwButton");
        registry.mapFeature(button, glfwButton, this::updateButton);
    }

    @Override
    @MustBeInvokedByOverriders
    public void initAdapter() {
        this.mapButton(BUTTON_M1, GLFW_MOUSE_BUTTON_1);
        this.mapButton(BUTTON_M2, GLFW_MOUSE_BUTTON_2);
        this.mapButton(BUTTON_M3, GLFW_MOUSE_BUTTON_3);
        this.mapButton(BUTTON_M4, GLFW_MOUSE_BUTTON_4);
        this.mapButton(BUTTON_M5, GLFW_MOUSE_BUTTON_5);
        this.mapButton(BUTTON_M6, GLFW_MOUSE_BUTTON_6);
        this.mapButton(BUTTON_M7, GLFW_MOUSE_BUTTON_7);
        this.mapButton(BUTTON_M8, GLFW_MOUSE_BUTTON_8);

        registry.mapFeature(FEATURE_CURSOR, this::updateCursor);

        CursorStateZ cursor = registry.getInternalState(FEATURE_CURSOR);
        cursor.adapterCanSetVisible = true;
        cursor.adapterCanSetPosition = true;
        cursor.adapterCanSetIcon = true;

        this.wasCursorVisible = cursor.visible;
    }

    /**
     * Updater for mouse buttons mapped via
     * {@link #mapButton(MouseButton, int)}.
     * <p>
     * todo
     *
     * @param state      the button state.
     * @param glfwButton the GLFW button.
     */
    @FeatureAdapter
    protected void updateButton(@NotNull MouseClickZ state, int glfwButton) {
        int status = glfwGetMouseButton(ptr_glfwWindow, glfwButton);
        state.pressed = status >= GLFW_PRESS;
    }

    /**
     * Updater for {@link Mouse#FEATURE_CURSOR}.
     * <p>
     * todo
     *
     * @param state the cursor state.
     */
    @FeatureAdapter
    protected void updateCursor(@NotNull CursorStateZ state) {
        Vector2fc requested = state.requestedPos;
        state.requestedPos = null;
        if (requested != null) {
            state.currentPos.set(requested);
            glfwSetCursorPos(ptr_glfwWindow, requested.x(), requested.y());
        } else {
            state.currentPos.x = (float) this.xPos[0];
            state.currentPos.y = (float) this.yPos[0];
        }

        if (!wasCursorVisible && state.visible) {
            glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            this.wasCursorVisible = true;
        } else if (wasCursorVisible && !state.visible) {
            glfwSetInputMode(ptr_glfwWindow, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            this.wasCursorVisible = false;
        }

        if (state.updatedIcon) {
            if (glfwCursorImage != null) {
                glfwCursorImage.free();
                glfwDestroyCursor(ptr_glfwCursor);
            }

            /*
             * If the icon for the cursor is null, it indicates the default
             * icon should be used. In GLFW, a NULL pointer represents the
             * default cursor.
             */
            if (state.icon == null) {
                this.glfwCursorImage = null;
                this.ptr_glfwCursor = 0L;
            } else {
                this.glfwCursorImage = createGlfwImage(state.icon);
                this.ptr_glfwCursor = glfwCreateCursor(glfwCursorImage, 0, 0);
            }
            glfwSetCursor(ptr_glfwWindow, ptr_glfwCursor);

            state.updatedIcon = false;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    protected void pollDevice() {
        glfwGetCursorPos(ptr_glfwWindow, xPos, yPos);
    }

    @Override
    protected final boolean isDeviceConnected() {
        return true; /* mouse is always connected */
    }

}
