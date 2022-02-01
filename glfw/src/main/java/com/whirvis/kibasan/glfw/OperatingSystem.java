package com.whirvis.kibasan.glfw;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Represents an operating system of which the program is running from. This
 * is used primarily by {@link GlfwJoystickSeeker} to determine which GUIDs
 * should be used when searching for joysticks. This is because GUIDs for the
 * same joystick are different across operating systems.
 *
 * @see #getCurrent()
 * @see #setNameSupplier(Supplier)
 * @see #addMask(String)
 * @see #satisfiesMask(String)
 */
public class OperatingSystem {

    /* @formatter:off */
    private static final @NotNull Map<String, OperatingSystem>
            REGISTERED = new HashMap<>();

    public static final @NotNull OperatingSystem
            WINDOWS = register(false,"windows", "Windows"),
            OSX = register(true, "osx", "Mac OSX"), 
            LINUX = register(true, "linux", "Linux"), 
            SOLARIS = register(true, "solaris", "Solaris");

    public static final @NotNull OperatingSystem
            ANDROID = register(true, "android", "Android"), 
            IOS = register(true, "ios", "IOS");

    private static @NotNull Supplier<String>
            nameSupplier = () -> System.getProperty("os.name");
    /* @formatter:on */

    private static boolean determinedCurrent;
    private static @Nullable OperatingSystem current;

    static {
        WINDOWS.addMasks("win");
        OSX.addMasks("mac", "darwin");
        LINUX.addMasks("linux", "ubuntu");
        SOLARIS.addMasks("solaris", "sun");
    }

    /**
     * Creates a new OS descriptor and registers it. The returned value is
     * the descriptor. This value should be cached somewhere for later use,
     * so it can be reused without need for {@link #getById(String)}.
     * <p/>
     * In order for {@link #getCurrent()}} to return this descriptor in
     * a search, this descriptor must be assigned one or more masks via
     * {@link #addMask(String)}.
     *
     * @param unix {@code true} if the OS is Unix based, {@code false}
     *             otherwise.
     * @param id   the system ID.
     * @param name the system display name.
     * @return the created OS descriptor.
     * @throws NullPointerException     if {@code id} or {@code name} are
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code id} is already being used
     *                                  by another registered OS.
     */
    public static @NotNull OperatingSystem register(boolean unix,
                                                    @NotNull String id,
                                                    @NotNull String name) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        if (REGISTERED.containsKey(id)) {
            throw new IllegalArgumentException("ID already registered");
        }

        OperatingSystem os = new OperatingSystem(unix, id, name);
        REGISTERED.put(id, os);
        return os;
    }

    /**
     * @param id the ID of the OS to fetch.
     * @return the registered OS with the specified ID, {@code null} if no
     * such OS has been registered.
     */
    public static @Nullable OperatingSystem getById(@NotNull String id) {
        Objects.requireNonNull(id, "id");
        return REGISTERED.get(id);
    }

    /**
     * @param os the OS to unregister.
     * @return {@code true} if {@code os} was unregistered, {@code false}
     * otherwise.
     * @throws NullPointerException if {@code os} is {@code null}.
     */
    public static boolean unregister(@NotNull OperatingSystem os) {
        Objects.requireNonNull(os, "os");
        if (REGISTERED.containsValue(os)) {
            REGISTERED.remove(os.id);
            return true;
        }
        return false;
    }

    /**
     * Sets the supplier used by this class to determine the name of this
     * operating system. By default, {@code System.getProperty("os.name")}
     * is used.
     *
     * @param supplier the supplier to use.
     * @throws NullPointerException if {@code supplier} is {@code null} or
     *                              supplies a {@code null} value.
     */
    public static void setNameSupplier(@NotNull Supplier<@NotNull String> supplier) {
        nameSupplier = Objects.requireNonNull(supplier, "supplier");
        Objects.requireNonNull(supplier.get(), "supplied name is null");
    }

    /**
     * @return the operating system this program is running on, {@code null}
     * if it cannot be determined.
     */
    public static @Nullable OperatingSystem getCurrent() {
        if (determinedCurrent) {
            return current;
        }

        String osName = nameSupplier.get();
        for (OperatingSystem os : REGISTERED.values()) {
            if (os.satisfiesMask(osName)) {
                current = os;
                break;
            }
        }

        determinedCurrent = true;
        return current;
    }

    public final boolean unix;
    public final @NotNull String id;
    public final @NotNull String name;
    private final @NotNull Set<@NotNull String> masks;

    private OperatingSystem(boolean unix, @NotNull String id,
                            @NotNull String name) {
        this.unix = unix;
        this.id = id;
        this.name = name;
        this.masks = new HashSet<>();
    }

    /**
     * @param mask the mask to add.
     * @throws NullPointerException if {@code mask} is {@code null}.
     */
    public void addMask(@NotNull String mask) {
        Objects.requireNonNull(mask, "mask");
        masks.add(mask.toLowerCase());
    }

    /**
     * @param masks the masks to add.
     * @throws NullPointerException if {@code masks} is {@code null}.
     */
    public void addMasks(@NotNull Iterable<@NotNull String> masks) {
        Objects.requireNonNull(masks, "masks");
        for (String osName : masks) {
            this.addMask(osName);
        }
    }

    /**
     * @param masks the masks to add.
     * @throws NullPointerException if {@code masks} is {@code null}.
     */
    public void addMasks(@NotNull String @NotNull ... masks) {
        Objects.requireNonNull(masks, "masks");
        this.addMasks(Arrays.asList(masks));
    }

    /**
     * @param mask the mask to remove.
     * @throws NullPointerException if {@code mask} is {@code null}.
     */
    public void removeMask(@NotNull String mask) {
        Objects.requireNonNull(mask, "mask");
        masks.remove(mask.toLowerCase());
    }

    /**
     * @param masks the masks to remove.
     * @throws NullPointerException if {@code masks} is {@code null}.
     */
    public void removeMasks(@NotNull Iterable<@NotNull String> masks) {
        Objects.requireNonNull(masks, "masks");
        for (String osName : masks) {
            this.removeMask(osName);
        }
    }

    /**
     * @param masks the masks to remove.
     * @throws NullPointerException if {@code masks} is {@code null}.
     */
    public void removeMasks(@NotNull String @NotNull ... masks) {
        Objects.requireNonNull(masks, "masks");
        this.removeMasks(Arrays.asList(masks));
    }

    /**
     * Returns whether the name of an OS satisfies one of the masks which
     * have been added to this descriptor. This is used to determine what
     * OS the program is running on via the name an OS reports.
     *
     * @param osName the OS name.
     * @return {@code true} if {@code osName} satisfies one of this
     * descriptor's mask, {@code false} otherwise.
     * @throws NullPointerException if {@code osName} is {@code null}.
     */
    public boolean satisfiesMask(@NotNull String osName) {
        Objects.requireNonNull(osName, "osName");
        osName = osName.toLowerCase();
        for (String mask : masks) {
            if (osName.contains(mask)) {
                return true;
            }
        }
        return false;
    }

}
