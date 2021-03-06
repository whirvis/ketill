package io.ketill.glfw;

import io.ketill.IoDevice;
import io.ketill.KetillException;
import io.ketill.controller.Controller;
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

/**
 * Scan for joysticks currently connected to a GLFW window.
 * <p>
 * When a sought after joystick connects to the window, the appropriate
 * {@link IoDevice} and adapter will be automatically instantiated. However,
 * after creation, they  must be polled manually. All discovered joysticks
 * can be polled using {@link #pollDevices()}.
 * <p>
 * <b>Requirements:</b> The {@code glfwPollEvents()} function <i>must</i>
 * be called before scanning. Failure to do so will result in out-of-date
 * device connection status being returned to the seeker by GLFW.
 * <p>
 * Furthermore, for a GLFW device seeker to work as expected, scans must
 * be performed periodically via {@link #seek()}. It is recommended to
 * perform a scan once every application update.
 * <p>
 * Finally, the child class <i>must</i> wrangle at least one GUID to tell
 * the seeker which GUIDs belong to which joystick. If this is neglected,
 * an {@code IllegalStateException} will be thrown.
 * <p>
 * <b>Thread safety:</b> This class is <i>not</i> thread-safe. Operations
 * like scanning must be run on the thread which created the GLFW window.
 *
 * @param <C> the controller type.
 * @see JsonDeviceGuids
 * @see GlfwJoystickAdapter
 */
public class GlfwJoystickSeeker<C extends Controller> extends GlfwDeviceSeeker<C> {

    private static final int JOYSTICK_COUNT = GLFW_JOYSTICK_LAST + 1;

    private static String getGuidResourcePath(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        return "/" + packageName.replaceAll("\\.", "/") + "/";
    }

    private final C[] joysticks;
    private final @NotNull Map<String, GlfwJoystickWrangler<C>> wranglers;
    private final String guidResourcePath;

    /**
     * Constructs a new {@code GlfwJoystickSeeker}.
     *
     * @param type           the GLFW joystick I/O device type.
     * @param ptr_glfwWindow the GLFW window pointer.
     * @throws NullPointerException if {@code type} is {@code null};
     *                              if {@code ptr_glfwWindow} is a null
     *                              pointer (has a value of zero).
     * @see #wrangleGuids(Iterable, GlfwJoystickWrangler)
     */
    @SuppressWarnings("unchecked")
    public GlfwJoystickSeeker(@NotNull Class<C> type, long ptr_glfwWindow) {
        super(ptr_glfwWindow);
        Objects.requireNonNull(type, "type cannot be null");

        this.joysticks = (C[]) Array.newInstance(type, JOYSTICK_COUNT);
        this.wranglers = new HashMap<>();
        this.guidResourcePath = getGuidResourcePath(this.getClass());
    }

    /**
     * Loads a JSON GUID file from the classpath and wrangles the stored
     * GUIDs for the current operating system. Note that the argument for
     * {@code path} can be either relative or absolute.
     * <p>
     * <b>Note:</b> When {@code path} starts with {@code "/"}, it is treated
     * as absolute. Otherwise, it is treated as a relative path. When the
     * path is relative, it starts from the location of this seeker's class
     * (the package that it resides in).
     *
     * @param path the path of the JSON file.
     * @return the loaded device GUIDs for the current system.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws KetillException      if an I/O error occurs; if no GUIDs
     *                              exist  for the current OS within the
     *                              loaded JSON file.
     * @see #wrangleGuids(Iterable, GlfwJoystickWrangler)
     */
    public @NotNull Collection<String> loadJsonGuids(@NotNull String path) {
        Objects.requireNonNull(path, "path cannot be null");
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
     * Returns if a GUID is currently wrangled.
     *
     * @param guid the joystick GUID, case-sensitive.
     * @return {@code true} if this device seeker is wrangling joysticks
     * with the specified GUID, {@code false} otherwise.
     * @throws NullPointerException if {@code guid} is {@code null}.
     * @see #isWranglingWith(String, GlfwJoystickWrangler)
     * @see #getWrangler(String)
     */
    public final boolean isWrangling(@NotNull String guid) {
        Objects.requireNonNull(guid, "guid cannot be null");
        return wranglers.containsKey(guid);
    }

    /**
     * Returns all GUID's currently being wrangled.
     * <p>
     * <b>Immutability:</b> The returned GUIDs are wrapped in an unmodifiable
     * collection. Attempting to modify it will result in an exception.
     *
     * @return all GUID's currently being wrangled.
     */
    public final @NotNull Collection<@NotNull String> getWrangled() {
        return Collections.unmodifiableSet(wranglers.keySet());
    }

    /**
     * Returns if a GUID is being wrangled with a given wrangler.
     *
     * @param guid     the joystick GUID, case-sensitive.
     * @param wrangler the joystick wrangler.
     * @return {@code true} if {@code wrangler} is wrangling joysticks
     * with the specified GUID, {@code false} otherwise.
     * @throws NullPointerException if {@code guid} or {@code wrangler}
     *                              are {@code null}.
     * @see #isWrangling(String)
     * @see #getWrangler(String)
     */
    public final boolean isWranglingWith(@NotNull String guid,
                                         @NotNull GlfwJoystickWrangler<C> wrangler) {
        Objects.requireNonNull(guid, "guid cannot be null");
        Objects.requireNonNull(wrangler, "wrangler cannot be null");
        return wranglers.get(guid) == wrangler;
    }

    /**
     * Returns the wrangler assigned to a given GUID.
     *
     * @param guid the joystick GUID, case-sensitive.
     * @return the wrangler assigned to {@code guid}, {@code null} if no
     * wrangler has been assigned.
     * @throws NullPointerException if {@code guid} is {@code null}.
     * @see #isWrangling(String)
     * @see #isWranglingWith(String, GlfwJoystickWrangler)
     */
    public final @Nullable GlfwJoystickWrangler<C> getWrangler(@NotNull String guid) {
        Objects.requireNonNull(guid, "guid cannot be null");
        return wranglers.get(guid);
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
     * @see #releaseGuid(String)
     * @see WranglerMethod
     */
    public void wrangleGuid(@NotNull String guid,
                            @NotNull GlfwJoystickWrangler<C> wrangler) {
        Objects.requireNonNull(guid, "guid cannot be null");
        Objects.requireNonNull(wrangler, "wrangler cannot be null");

        /*
         * Not checking for the current wrangler assigned to GUID would lead
         * to some weird side effects. For example, if another wrangler was
         * previously assigned to the GUID. If a joystick was discovered with
         * that wrangler, it would linger on with an outdated wrangler.
         */
        GlfwJoystickWrangler<C> currentWrangler = wranglers.get(guid);
        if (currentWrangler != null) {
            if (currentWrangler == wrangler) {
                return; /* nothing to do */
            } else {
                this.releaseGuid(guid);
            }
        }

        wranglers.put(guid, wrangler);
        this.guidWrangled(guid, wrangler);
        observer.onNext(new WrangleGuidEvent(this, guid, wrangler));
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
     * <b>Shorthand for:</b>
     * {@link #wrangleGuid(String, GlfwJoystickWrangler)}, with each element
     * of {@code guids} being passed as the argument for {@code guid}.
     *
     * @param toWrangle the joystick GUIDs, case-sensitive.
     * @param wrangler  the joystick wrangler.
     * @throws NullPointerException if {@code guids} or {@code wrangler}
     *                              are {@code null}; if an element of
     *                              {@code guids} is {@code null}.
     * @see #releaseGuids(Collection)
     * @see WranglerMethod
     */
    public final void wrangleGuids(@NotNull Iterable<@NotNull String> toWrangle,
                                   @NotNull GlfwJoystickWrangler<C> wrangler) {
        Objects.requireNonNull(toWrangle, "toWrangle cannot be null");
        for (String guid : toWrangle) {
            this.wrangleGuid(guid, wrangler);
        }
    }

    /**
     * Called when a GUID is being wrangled. This will be called before
     * the corresponding event is emitted to subscribers.
     * <p>
     * <b>Reentrancy:</b>
     * Invoking {@link #wrangleGuid(String, GlfwJoystickWrangler)} inside
     * of this method will likely result in a {@code StackOverflowError}
     * unless proper care is taken to ensure otherwise.
     *
     * @param guid     the GUID.
     * @param wrangler the wrangler assigned to {@code guid}.
     */
    protected void guidWrangled(@NotNull String guid,
                                @NotNull GlfwJoystickWrangler<C> wrangler) {
        /* optional implement */
    }

    /**
     * Unbinds the given GUIDs from their joystick wranglers.
     * <p>
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     *
     * @param toRelease the GUIDs to release, case-sensitive.
     * @throws NullPointerException if {@code toRelease} is {@code null};
     *                              if an element of {@code toRelease} is
     *                              {@code null}.
     * @see #wrangleGuids(Iterable, GlfwJoystickWrangler)
     */
    public void releaseGuids(@NotNull Collection<@NotNull String> toRelease) {
        Objects.requireNonNull(toRelease, "toRelease cannot be null");

        /*
         * Since access to the joysticks array is not synchronized, the
         * seekImpl() method could be invoked while this method is still
         * being executed. If this method forgets a device while seekImpl()
         * is still running, the joystick could be rediscovered in error.
         *
         * To prevent this occurring, simply remove the associated wrangler
         * from each GUID to drop before forgetting them. This ensures they
         * will not be rediscovered by seekImpl().
         */
        for (String guid : toRelease) {
            Objects.requireNonNull(guid, "guid cannot be null");
            GlfwJoystickWrangler<C> wrangler = wranglers.remove(guid);
            if (wrangler != null) {
                this.guidReleased(guid, wrangler);
                observer.onNext(new ReleaseGuidEvent(this, guid, wrangler));
            }
        }

        /*
         * If a joystick's GUID matches one of the dropped GUIDs, it must be
         * unregistered here. It makes no sense for a joystick that would no
         *  longer be registered to linger.
         */
        for (int i = 0; i < joysticks.length; i++) {
            /*
             * In rare occasions (so far, this has been observed only in
             * unit testing), an undiscovered joystick with have a GUID
             * that is due to be released. As such, joysticks which have
             * yet to be discovered must be skipped over.
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
     * Unbinds a GUID from its joystick wrangler.
     * <p>
     * All currently registered joysticks with a matching GUID will be
     * automatically unregistered. This is to prevent the connection of
     * undesired joysticks from lingering.
     * <p>
     * <b>Shorthand for:</b> {@link #releaseGuids(Collection)}, with
     * {@code toDrop} being passed as a collection via
     * {@link Collections#singletonList(Object)}.
     *
     * @param toRelease the GUID to release, case-sensitive.
     * @throws NullPointerException if {@code toDrop} is {@code null}.
     * @see #wrangleGuid(String, GlfwJoystickWrangler)
     */
    public final void releaseGuid(@NotNull String toRelease) {
        Objects.requireNonNull(toRelease, "toRelease cannot be null");
        this.releaseGuids(Collections.singletonList(toRelease));
    }

    /**
     * Called when a GUID has been released. This will be called before
     * the corresponding event is emitted to subscribers.
     * <p>
     * <b>Reentrancy:</b> Invoking {@link #releaseGuid(String)} inside
     * of this method will likely result in a {@code StackOverflowError}
     * unless proper care is taken to ensure otherwise.
     *
     * @param guid     the GUID.
     * @param wrangler the wrangler assigned to {@code guid}.
     */
    protected void guidReleased(@NotNull String guid,
                                @NotNull GlfwJoystickWrangler<C> wrangler) {
        /* optional implement */
    }

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() {
        if (wranglers.isEmpty()) {
            throw new IllegalStateException("no GUIDs wrangled");
        }

        for (int i = 0; i < joysticks.length; i++) {
            String guid = glfwGetJoystickGUID(i);

            C joystick = this.joysticks[i];
            if (joystick != null) {
                /*
                 * Although joystick.isConnected() is generally trusted, this
                 * test against a null GUID ensures a zombie adapter cannot
                 * hold a newer joystick hostage if it decides to return true
                 * even when it is not actually connected.
                 */
                if (guid == null || !joystick.isConnected()) {
                    this.forgetDevice(joystick);
                    this.joysticks[i] = null;
                }
                continue;
            }

            /*
             * If not present, glfwGetJoystickGUID() returns null for the
             * GUID. This makes a call to glfwJoystickPresent() redundant.
             */
            if (guid != null && this.isWrangling(guid)) {
                GlfwJoystickWrangler<C> wrangler = wranglers.get(guid);

                C wrangled = wrangler.wrangleDevice(ptr_glfwWindow, i);
                {
                    String msg = wrangler.getClass().getName();
                    msg += " assigned to device GUID " + guid;
                    msg += " returned a null device";
                    Objects.requireNonNull(wrangled, msg);
                }

                this.joysticks[i] = wrangled;
                this.discoverDevice(joysticks[i]);
            }
        }
    }

}
