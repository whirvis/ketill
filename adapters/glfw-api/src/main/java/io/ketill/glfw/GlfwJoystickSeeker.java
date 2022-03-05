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
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;


/**
 * GLFW joysticks seekers scan for I/O devices currently connected to a
 * GLFW window as a joystick. When a joystick is connected to the window,
 * the appropriate {@code IoDevice} instance and adapter will be created
 * using a {@link GlfwJoystickWrangler}. Devices must be polled manually
 * after creation using {@link IoDevice#poll()}. They can be retrieved from
 * {@link #discoveredDevices}.
 * <p>
 * Implementations should call
 * {@link #wrangleGuid(String, GlfwJoystickWrangler)} to tell the seeker
 * what GUIDs belong to which joystick. When a joystick with a matching
 * GUID is located, it will be wrangled and then discovered automatically.
 * <p>
 * <b>Note:</b> For a GLFW joystick seeker to work as expected, scans must be
 * performed periodically via {@link #seek()}. It is recommended to perform
 * a scan once every application update.
 *
 * @param <I> the I/O device type.
 * @see #onDiscoverDevice(Consumer)
 * @see #onForgetDevice(Consumer)
 * @see #onSeekError(Consumer)
 * @see RelativeGuidPath
 * @see JsonDeviceGuids
 * @see GlfwJoystickAdapter
 */
public class GlfwJoystickSeeker<I extends IoDevice>
        extends GlfwDeviceSeeker<I> {

    private static final int JOYSTICK_COUNT = GLFW_JOYSTICK_LAST + 1;

    private static String getGuidResourcePath(Class<?> clazz) {
        RelativeGuidPath guidResourcePath =
                clazz.getAnnotation(RelativeGuidPath.class);
        if (guidResourcePath == null) {
            String packageName = clazz.getPackage().getName();
            return "/" + packageName.replaceAll("\\.", "/");
        }

        String value = guidResourcePath.value();
        if (!value.startsWith("/") || !value.endsWith("/")) {
            String msg = "@" + RelativeGuidPath.class.getSimpleName();
            msg += " value must start and end with a forward slash (\"/\")";
            throw new KetillException(msg);
        }
        return value;
    }

    private final I[] joysticks;
    private final @NotNull Map<String, GlfwJoystickWrangler<I>> wranglers;
    private final @NotNull String guidResourcePath;

    private @Nullable GuidCallback seekGuidCallback;
    private @Nullable GuidCallback releaseGuidCallback;

    /**
     * @param type           the GLFW joystick I/O device type.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException     if {@code type} is {@code null};
     *                                  if {@code ptr_glfwWindow} is a null
     *                                  pointer (has a value of zero.)
     * @throws IllegalArgumentException if {@code ptr_glfwWindow} is not a
     *                                  valid GLFW window pointer.
     * @see #loadJsonGuids(String)
     * @see #wrangleGuids(Iterable, GlfwJoystickWrangler)
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
     * The argument for {@code path} can be either relative or absolute.
     * <p>
     * When {@code path} starts with {@code "/"}, it is treated as
     * absolute. Otherwise, it is treated as a relative path. When
     * the path is relative, it starts from the location of this
     * seeker's class (the package it resides in).
     *
     * @param path the path of the JSON file.
     * @return the loaded device GUIDs for the current system.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws KetillException      if an I/O error occurs; if no GUIDs
     *                              exist  for the current OS within the
     *                              loaded JSON file.
     * @see RelativeGuidPath
     * @see #wrangleGuids(Iterable, GlfwJoystickWrangler)
     */
    protected final @NotNull Collection<String> loadJsonGuids(@NotNull String path) {
        Objects.requireNonNull(path, "path");
        try {
            String fullPath = path;
            if (!path.startsWith("/")) {
                fullPath = this.guidResourcePath + path;
            }

            DeviceGuids guids = JsonDeviceGuids.loadResource(fullPath);
            Collection<String> loaded = guids.getSystemGuids();
            if (loaded == null) {
                throw new KetillException("no GUIDs for current OS");
            }

            return loaded;
        } catch (IOException e) {
            throw new KetillException("failed to load resource", e);
        }
    }

    /**
     * Sets the callback for when this seeker begins wrangling a GUID.
     *
     * @param callback the code to execute when a GUID is wrangled. A
     *                 value of {@code null} is permitted, and will
     *                 result in nothing being executed.
     */
    public final void onWrangleGuid(@Nullable GuidCallback callback) {
        this.seekGuidCallback = callback;
    }

    /**
     * Sets the callback for when this seeker releases a GUID.
     *
     * @param callback the code to execute when a GUID is released. A
     *                 value of {@code null} is permitted, and will
     *                 result in nothing being executed.
     */
    public final void onReleaseGuid(@Nullable GuidCallback callback) {
        this.releaseGuidCallback = callback;
    }

    /**
     * @param guid the joystick GUID, case-sensitive.
     * @return {@code true} if this device seeker is wrangling joysticks
     * with the specified GUID, {@code false} otherwise.
     * @throws NullPointerException if {@code guid} is {@code null}.
     * @see #isWranglingWith(String, GlfwJoystickWrangler)
     */
    public final boolean isWrangling(@NotNull String guid) {
        Objects.requireNonNull(guid, "guid");
        return wranglers.containsKey(guid);
    }

    /**
     * @param guid     the joystick GUID, case-sensitive.
     * @param wrangler the joystick wrangler.
     * @return {@code true} if {@code wrangler} is wrangling joysticks
     * with the specified GUID, {@code false} otherwise.
     * @throws NullPointerException if {@code guid} or {@code wrangler}
     *                              are {@code null}.
     * @see #isWrangling(String)
     */
    public final boolean isWranglingWith(@NotNull String guid,
                                         @NotNull GlfwJoystickWrangler<I> wrangler) {
        Objects.requireNonNull(guid, "guid");
        Objects.requireNonNull(wrangler, "wrangler");
        return wranglers.get(guid) == wrangler;
    }

    /**
     * Binds the GUID of a joystick to a joystick wrangler. When a joystick
     * should be registered, {@code wrangler} will be invoked to create the
     * device, so it can then be discovered.
     * <p>
     * <b>Note:</b> For proper state management, if {@code guid} has been
     * assigned to another wrangler, it will be released. However, if the
     * argument for {@code wrangler} matches the wrangler {@code guid} is
     * currently assigned to, this method will be a no-op.
     *
     * @param guid     the joystick GUID, case-sensitive.
     * @param wrangler the joystick wrangler.
     * @throws NullPointerException if {@code guid} or {@code wrangler}
     *                              are {@code null}.
     * @see #onWrangleGuid(GuidCallback)
     * @see #releaseGuid(String)
     */
    protected final void wrangleGuid(@NotNull String guid,
                                     @NotNull GlfwJoystickWrangler<I> wrangler) {
        Objects.requireNonNull(guid, "guid");
        Objects.requireNonNull(wrangler, "wrangler");

        /*
         * Not checking for the current wrangler assigned to GUID
         * would lead to some weird side effects. For example, if
         * another wrangler was previously assigned to the GUID.
         * If a joystick was discovered with that wrangler, it
         * would linger on with an outdated wrangler.
         */
        GlfwJoystickWrangler<I> currentWrangler = wranglers.get(guid);
        if (currentWrangler != null) {
            if (currentWrangler == wrangler) {
                return; /* nothing to do */
            } else {
                this.releaseGuid(guid);
            }
        }

        wranglers.put(guid, wrangler);
        if (seekGuidCallback != null) {
            seekGuidCallback.execute(guid, wrangler);
        }
    }

    /**
     * Binds the GUIDs of a joystick to a joystick wrangler. When a joystick
     * should be registered, {@code wrangler} will be invoked to create the
     * device, so it can then be discovered.
     * <p>
     * <b>Note:</b> For proper state management, if one of the specified
     * GUIDs has been assigned to another wrangler, it will be released.
     * However, if the argument for {@code wrangler} matches its current
     * wrangler, nothing will occur.
     * <p>
     * This method is a shorthand for
     * {@link #wrangleGuid(String, GlfwJoystickWrangler)}, with each element
     * being passed as the argument for {@code guid}.
     *
     * @param guids    the joystick GUIDs, case-sensitive.
     * @param wrangler the joystick wrangler.
     * @throws NullPointerException if {@code guids} or {@code wrangler}
     *                              are {@code null}; if an element of
     *                              {@code guids} is {@code null}.
     * @see #onWrangleGuid(GuidCallback)
     * @see #releaseGuids(Collection)
     */
    protected final void wrangleGuids(@NotNull Iterable<@NotNull String> guids,
                                      @NotNull GlfwJoystickWrangler<I> wrangler) {
        Objects.requireNonNull(guids, "guids");
        for (String guid : guids) {
            this.wrangleGuid(guid, wrangler);
        }
    }

    /**
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     *
     * @param toRelease the GUIDs to release, case-sensitive.
     * @throws NullPointerException if {@code toRelease} is {@code null};
     *                              if an element of {@code toRelease} is
     *                              {@code null}.
     * @see #onReleaseGuid(GuidCallback)
     * @see #wrangleGuids(Iterable, GlfwJoystickWrangler)
     */
    protected final void releaseGuids(@NotNull Collection<@NotNull String> toRelease) {
        Objects.requireNonNull(toRelease, "toRelease");

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
        for (String guid : toRelease) {
            Objects.requireNonNull(guid, "guid");
            GlfwJoystickWrangler<?> wrangler = wranglers.remove(guid);
            if (wrangler != null && releaseGuidCallback != null) {
                releaseGuidCallback.execute(guid, wrangler);
            }
        }

        /*
         * If a joystick's GUID matches one of the dropped GUIDs,
         * it must be unregistered here. It makes no sense for a
         * joystick that would no longer be registered to linger.
         */
        for (int i = 0; i < joysticks.length; i++) {
            /*
             * In rare occasions (so far, this has been observed
             * only on unit testing), an undiscovered joystick
             * with have a GUID that is due to be released. As
             * such, joysticks which have yet to be discovered
             * must be skipped over.
             */
            if (joysticks[i] == null) {
                continue;
            }

            String guid = glfwGetJoystickGUID(i);
            if (guid != null && toRelease.contains(guid)) {
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
     * This method is a shorthand for {@link #releaseGuids(Collection)}, with
     * {@code toDrop} being passed as a collection via
     * {@link Collections#singletonList(Object)}.
     *
     * @param toRelease the GUID to release, case-sensitive.
     * @throws NullPointerException if {@code toDrop} is {@code null}.
     * @see #onReleaseGuid(GuidCallback)
     * @see #wrangleGuid(String, GlfwJoystickWrangler)
     */
    protected final void releaseGuid(@NotNull String toRelease) {
        Objects.requireNonNull(toRelease, "toRelease");
        this.releaseGuids(Collections.singletonList(toRelease));
    }

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() {
        for (int i = 0; i < joysticks.length; i++) {
            String guid = glfwGetJoystickGUID(i);

            I joystick = this.joysticks[i];
            if (joystick != null) {
                /*
                 * Although joystick.isConnected() is generally
                 * trusted, this test against a null GUID ensures
                 * a zombie adapter cannot hold a newer joystick
                 * hostage if it decides to return true even when
                 * it is not actually connected.
                 */
                if (guid == null || !joystick.isConnected()) {
                    this.forgetDevice(joystick);
                    this.joysticks[i] = null;
                }
                continue;
            }

            /*
             * If not present, glfwGetJoystickGUID() returns
             * null or the joystick GUID. This makes a call
             * to glfwJoystickPresent() redundant.
             */
            if (guid != null && this.isWrangling(guid)) {
                GlfwJoystickWrangler<I> wrangler = wranglers.get(guid);
                I wrangled = wrangler.wrangleDevice(ptr_glfwWindow, i);
                Objects.requireNonNull(wrangled,
                        "wrangler for device GUID " + guid
                                + "returned null device");
                this.joysticks[i] = wrangled;
                this.discoverDevice(joysticks[i]);
            }
        }
    }

}
