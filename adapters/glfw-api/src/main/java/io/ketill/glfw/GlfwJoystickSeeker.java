package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.KetillException;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class GlfwJoystickSeeker<I extends IoDevice> extends GlfwDeviceSeeker<I> {

    private static String getGuidResourcePath(Class<?> clazz) {
        GuidResourcePath guidResourcePath =
                clazz.getAnnotation(GuidResourcePath.class);
        if (guidResourcePath == null) {
            String packageName = clazz.getPackage().getName();
            return "/" + packageName.replaceAll("\\.", "/");
        }

        String value = guidResourcePath.value();
        if (value.endsWith("/")) {
            String msg = "@" + GuidResourcePath.class.getSimpleName();
            msg += " value cannot end with a forward slash (\"/\")";
            throw new KetillException(msg);
        }
        return value;
    }

    private static final int JOYSTICK_COUNT = GLFW_JOYSTICK_LAST + 1;

    private final I[] joysticks;
    private final @NotNull Map<String, GlfwJoystickWrangler<I>> wranglers;
    private final @NotNull String guidResourcePath;

    private @Nullable GuidCallback seekGuidCallback;
    private @Nullable GuidCallback dropGuidCallback;

    /**
     * @param type           the GLFW joystick I/O device type.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code type} is {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     * @see GuidResourcePath
     * @see #loadJsonGuids(String)
     * @see #seekGuid(String, GlfwJoystickWrangler)
     */
    @SuppressWarnings("unchecked")
    public GlfwJoystickSeeker(@NotNull Class<I> type, long ptr_glfwWindow) {
        super(ptr_glfwWindow);
        Objects.requireNonNull(type, "type");

        this.joysticks = (I[]) Array.newInstance(type, JOYSTICK_COUNT);
        this.wranglers = new HashMap<>();
        this.guidResourcePath = getGuidResourcePath(this.getClass());
    }

    /**
     * TODO: docs
     */
    /* @formatter:off */
    protected final @NotNull Collection<String>
            loadJsonGuids(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        try {
            String fullPath = this.guidResourcePath + path;
            DeviceGuids guids = JsonDeviceGuids.loadResource(fullPath);
            Collection<String> loaded = guids.getSystemGuids();

            if (loaded == null) {
                throw new KetillException("could not determine OS");
            }

            return loaded;
        } catch (IOException e) {
            throw new KetillException("failed to load resource", e);
        }
    }
    /* @formatter:on */

    /**
     * Sets the callback for when this seeker begins seeking a GUID.
     *
     * @param callback the code to execute when a GUID is sought after. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     */
    public final void onSeekGuid(@Nullable GuidCallback callback) {
        this.seekGuidCallback = callback;
    }

    /**
     * Sets the callback for when this seeker stops seeking a GUID.
     *
     * @param callback the code to execute when a GUID is dropped. A value
     *                 of {@code null} is permitted, and will result in
     *                 nothing being executed.
     */
    public final void onDropGuid(@Nullable GuidCallback callback) {
        this.dropGuidCallback = callback;
    }

    /**
     * @param guid the joystick GUID, case-sensitive.
     * @return {@code true} if this device seeker is seeking out joysticks with
     * the specified GUID, {@code false} otherwise.
     */
    public final boolean isSeeking(@NotNull String guid) {
        return wranglers.containsKey(guid);
    }

    /**
     * Binds the GUID of a joystick to a joystick wrangler. When a joystick
     * is detected, its GUID will be checked to see if the seeker should
     * register it. When a joystick should be registered, {@code wrangler}
     * will be invoked to create the device, so it can be discovered.
     *
     * @param guid     the joystick GUID, case-sensitive.
     * @param wrangler the joystick wrangler.
     */
    protected final void seekGuid(@NotNull String guid,
                                  @NotNull GlfwJoystickWrangler<I> wrangler) {
        wranglers.put(guid, wrangler);
        if (seekGuidCallback != null) {
            seekGuidCallback.execute(guid, wrangler);
        }
    }

    /**
     * Binds the GUID of a joystick to a joystick wrangler. When a joystick
     * is detected, its GUID will be checked to see if the seeker should
     * register it. When a joystick should be registered, {@code wrangler}
     * will be invoked to create the device, so it can be discovered.
     * <p>
     * This method is a shorthand for
     * {@link #seekGuid(String, GlfwJoystickWrangler)}, with each element of
     * {@code guids} being passed as the argument for {@code guid}.
     *
     * @param guids    the joystick GUIDs, case-sensitive.
     * @param wrangler the joystick wrangler.
     */
    protected final void seekGuids(@NotNull Iterable<@NotNull String> guids,
                                   @NotNull GlfwJoystickWrangler<I> wrangler) {
        for (String guid : guids) {
            this.seekGuid(guid, wrangler);
        }
    }

    /**
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     *
     * @param toDrop the GUIDs to drop, case-sensitive.
     */
    protected final void dropGuids(@NotNull Collection<@NotNull String> toDrop) {
        /*
         * Since access to the joysticks array is not synchronized,
         * the seekImpl() method could be invoked while this method
         * is still being executed. If this method forgets a device
         * while seekImpl() is still running, the joystick could be
         * rediscovered in error.
         *
         * To prevent this occurring, simply remove the associated
         * wrangler from each GUID to drop before forgetting them.
         * This ensures seekImpl() will not rediscover them.
         */
        for (String guid : toDrop) {
            GlfwJoystickWrangler<?> wrangler = wranglers.remove(guid);
            if (wrangler != null && dropGuidCallback != null) {
                dropGuidCallback.execute(guid, wrangler);
            }
        }

        /*
         * If a joystick's GUID matches one of the dropped GUIDs,
         * it must be unregistered here. It makes no sense for a
         * joystick that would no longer be registered to linger.
         */
        for (int i = 0; i < joysticks.length; i++) {
            String guid = glfwGetJoystickGUID(i);
            if (guid != null && toDrop.contains(guid)) {
                this.forgetDevice(joysticks[i]);
                this.joysticks[i] = null;
            }
        }
    }

    /**
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     * <p>
     * This method is a shorthand for {@link #dropGuids(Collection)}, with
     * {@code toDrop} being passed as a collection via
     * {@link Collections#singletonList(Object)}.
     *
     * @param toDrop the GUID to drop, case-sensitive.
     */
    protected final void dropGuid(@NotNull String toDrop) {
        this.dropGuids(Collections.singletonList(toDrop));
    }

    @Override
    @MustBeInvokedByOverriders
    public void seekImpl() {
        for (int i = 0; i < joysticks.length; i++) {
            I joystick = this.joysticks[i];
            if (joystick != null) {
                if (!joystick.isConnected()) {
                    this.forgetDevice(joystick);
                    this.joysticks[i] = null;
                }
                continue;
            }

            /*
             * If the joystick is not present, glfwGetJoystickGUID() returns
             * null. This makes a call to glfwJoystickPresent() redundant.
             */
            String guid = glfwGetJoystickGUID(i);
            if (guid != null && this.isSeeking(guid)) {
                GlfwJoystickWrangler<I> wrangler = wranglers.get(guid);
                this.joysticks[i] = wrangler.wrangleDevice(ptr_glfwWindow, i);
                this.discoverDevice(joysticks[i]);
            }
        }
    }

}
