package io.ketill.glfw;

import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A container for the GUIDs a device.
 * <p>
 * This should be used with {@link GlfwJoystickSeeker} to determine which set
 * of GUIDs to use when seeking for a joystick. The GUIDs of a joystick vary
 * across different operating systems. This container can determine which
 * GUIDs to return automatically via {@link #getSystemGuids()}.
 *
 * @see #addSystem(String, OsDeterminant)
 */
public abstract class DeviceGuids {

    /**
     * System IDs for the operating systems supported by default.
     */
    /* @formatter:off */
    public static final @NotNull String
            ID_WINDOWS = "windows",
            ID_LINUX   = "linux",
            ID_MAC_OSX = "mac_osx",
            ID_ANDROID = "android";
    /* @formatter:on */

    private static final OsDeterminant ANDROID_DETERMINANT = () -> {
        String runtimeName = System.getProperty("java.runtime.name");
        if (runtimeName == null) {
            return false;
        }
        return runtimeName.toLowerCase().contains("android");
    };

    private static final OsDeterminant LINUX_DETERMINANT = () -> {
        /*
         * Android devices run on Linux. Just to be safe, ensure that the
         * current operating system is not Android before returning if it's
         * Linux. Otherwise, two of the default determinants could report
         * they are the current system.
         */
        if (ANDROID_DETERMINANT.isCurrentOs()) {
            return false;
        }
        return SystemUtils.IS_OS_LINUX;
    };

    private final Map<String, OsDeterminant> systems;

    /**
     * Constructs a new instance of {@code DeviceGuids}.
     * <p>
     * If the argument for {@code useDefaultSystems} is set to {@code true},
     * determinants for the following systems are added:
     * <ul>
     *     <li>Windows (ID: {@code "windows"})</li>
     *     <li>Mac OSX (ID: {@code "mac_osx"})</li>
     *     <li>Linux (ID: {@code "linux"})</li>
     *     <li>Android (ID: {@code "android"})</li>
     * </ul>
     * Because it lacks an officially supported JVM, iOS is excluded from
     * this list. Support for more systems can be added using
     * {@link #addSystem(String, OsDeterminant)}.
     *
     * @param useDefaultSystems {@code true} if the default operating systems
     *                          should be added, {@code false} otherwise.
     */
    public DeviceGuids(boolean useDefaultSystems) {
        this.systems = new HashMap<>();
        if (useDefaultSystems) {
            this.addSystem(ID_WINDOWS, () -> SystemUtils.IS_OS_WINDOWS);
            this.addSystem(ID_MAC_OSX, () -> SystemUtils.IS_OS_MAC_OSX);
            this.addSystem(ID_LINUX, LINUX_DETERMINANT);
            this.addSystem(ID_ANDROID, ANDROID_DETERMINANT);
        }
    }

    /**
     * Constructs a new instance of {@code DeviceGuids} with determinants
     * for the following operating systems already added:
     * <ul>
     *     <li>Windows (ID: {@code "windows"})</li>
     *     <li>Mac OSX (ID: {@code "mac_osx"})</li>
     *     <li>Linux (ID: {@code "linux"})</li>
     *     <li>Android (ID: {@code "android"})</li>
     * </ul>
     * Because it lacks an officially supported JVM, iOS is excluded from
     * this list. Support for more systems can be added using
     * {@link #addSystem(String, OsDeterminant)}.
     */
    public DeviceGuids() {
        this(true);
    }

    private List<String> getCurrentSystemIds() {
        List<String> systemIds = new ArrayList<>();
        for (String id : systems.keySet()) {
            OsDeterminant determinant = systems.get(id);
            if (determinant.isCurrentOs()) {
                systemIds.add(id);
            }
        }
        return systemIds;
    }

    private String getCurrentSystemId() {
        List<String> systemIds = this.getCurrentSystemIds();
        return systemIds.size() == 1 ? systemIds.get(0) : null;
    }

    /**
     * @param systemId the system ID to check for.
     * @return {@code true} if this container supports the system with the
     * specified ID, {@code false} otherwise.
     * @throws NullPointerException if {@code systemId} is {@code null}.
     */
    public final boolean supportsSystem(@NotNull String systemId) {
        Objects.requireNonNull(systemId, "systemId cannot be null");
        return systems.containsKey(systemId);
    }

    /**
     * @param systemId    the system ID.
     * @param determinant the determinant.
     * @throws NullPointerException     if {@code systemId} or
     *                                  {@code determinant} are {@code null}.
     * @throws IllegalArgumentException if {@code systemId} is empty
     *                                  or contains whitespace.
     * @throws IllegalStateException    if more than one OS determinant states
     *                                  they're the current operating system
     *                                  as a result of adding this system.
     */
    public final void addSystem(@NotNull String systemId,
                                @NotNull OsDeterminant determinant) {
        Objects.requireNonNull(systemId, "systemId cannot be null");
        Objects.requireNonNull(determinant, "determinant cannot be null");

        if (systemId.isEmpty()) {
            String msg = "systemId cannot be empty";
            throw new IllegalArgumentException(msg);
        } else if (!systemId.matches("\\S+")) {
            String msg = "systemId cannot contain whitespace";
            throw new IllegalArgumentException(msg);
        }

        systems.put(systemId, determinant);

        /*
         * If there is more than one possible OS, it means multiple
         * determinants are reporting they are the current OS. This
         * is unacceptable, as it makes it impossible to determine
         * what the current operating system is.
         */
        List<String> systemIds = this.getCurrentSystemIds();
        if (systemIds.size() > 1) {
            systems.remove(systemId); /* don't let it linger */
            throw new IllegalStateException("conflicting determinants");
        }
    }

    /**
     * @param systemId the system ID.
     * @return {@code true} if an operating system with the specified ID
     * was removed, {@code false} otherwise.
     * @throws NullPointerException if {@code systemId} is {@code null}.
     */
    public final boolean removeSystem(@NotNull String systemId) {
        Objects.requireNonNull(systemId, "systemId cannot be null");
        return systems.remove(systemId) != null;
    }

    /**
     * Implementation for {@link #getGuids(String)}. The returned collection
     * will be wrapped so that it is unmodifiable by the caller.
     *
     * @param systemId the operating system ID.
     * @return the GUIDs for the device when running on an OS with the
     * specified ID. If no such set of GUIDs exists, then this method
     * should return {@code null} (<i>not</i> an empty collection).
     */
    /* @formatter:off */
    protected abstract @Nullable Collection<@NotNull String>
            getGuidsImpl(@NotNull String systemId);
    /* @formatter:on */

    /**
     * @param systemId the operating system ID.
     * @return the GUIDs for the device when running on an OS with the
     * specified ID, {@code null} if no such set of GUIDs exists.
     * @throws NullPointerException if {@code systemId} is {@code null}.
     * @see #getSystemGuids()
     */
    /* @formatter:off */
    public final @Nullable Collection<@NotNull String>
            getGuids(@NotNull String systemId) {
        Objects.requireNonNull(systemId, "systemId cannot be null");
        Collection<String> guids = this.getGuidsImpl(systemId);
        if(guids == null) {
            return null;
        }
        return Collections.unmodifiableCollection(guids);
    }
    /* @formatter:on */

    /**
     * @return the GUIDs for the device on the current operating system,
     * {@code null} if the OS could not be determined or no GUIDs exist
     * for the current OS.
     */
    public final @Nullable Collection<@NotNull String> getSystemGuids() {
        String systemId = this.getCurrentSystemId();
        if (systemId == null) {
            return null; /* unknown OS */
        }
        return this.getGuids(systemId);
    }

}
