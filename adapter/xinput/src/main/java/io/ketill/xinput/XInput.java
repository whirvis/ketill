package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.XInputLibraryVersion;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import com.github.strikerx3.jxinput.natives.XInputNatives;
import io.ketill.xbox.XboxController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The base class for XInput operations.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i>
 *
 * @see #isAvailable()
 * @see #getPlayer(int)
 * @see XInputXboxSeeker
 */
public final class XInput {

    /**
     * The available player count.
     */
    public static final int PLAYER_COUNT = XInputConstants.MAX_PLAYERS;

    /**
     * A battery level approximation.
     *
     * @see XboxController#INTERNAL_BATTERY
     */
    /* @formatter:off */
    public static final float
            BATTERY_LEVEL_UNKNOWN  = -1.00F,
            BATTERY_LEVEL_EMPTY    =  0.00F,
            BATTERY_LEVEL_LOW      =  0.25F,
            BATTERY_LEVEL_MEDIUM   =  0.50F,
            BATTERY_LEVEL_FULL     =  1.00F;
    /* @formatter:on */

    /* @formatter:off */
    private static final Lock
            CONTROLLERS_LOCK = new ReentrantLock();
    private static final AtomicBoolean
            CONTROLLERS_CACHED = new AtomicBoolean();
    private static final AtomicReference<XInputException>
            CONTROLLERS_ERROR = new AtomicReference<>();
    private static final XboxController[]
            CONTROLLERS = new XboxController[PLAYER_COUNT];
    /* @formatter:on */

    private XInput() {
        /* prevent instantiation */
    }

    @VisibleForTesting
    static void reset() {
        CONTROLLERS_LOCK.lock();
        try {
            CONTROLLERS_CACHED.set(false);
            CONTROLLERS_ERROR.set(null);
            Arrays.fill(CONTROLLERS, null);
        } finally {
            CONTROLLERS_LOCK.unlock();
        }
    }

    /**
     * Returns if XInput is available on this machine.
     * <p>
     * Since XInput may not be available on the current system, it is
     * best to check for availability before attempting to use the rest
     * of this module.
     *
     * @return {@code true} if the XInput library is available,
     * {@code false} otherwise.
     * @see #requireAvailable()
     */
    public static boolean isAvailable() {
        /*
         * XInputNatives must be used here. Using XInputDevice will
         * result in a NoClassDefFoundError on non-Windows systems.
         */
        return XInputNatives.isLoaded();
    }

    /**
     * Requires that XInput be available on this machine before
     * continuing execution.
     *
     * @throws XInputUnavailableException if the XInput library is not
     *                                    available on this machine.
     * @see #isAvailable()
     */
    public static void requireAvailable() {
        if (!isAvailable()) {
            throw new XInputUnavailableException();
        }
    }

    /**
     * Returns the current version of XInput.
     *
     * @return the current version of XInput, {@code null} if it is not
     * available on this machine.
     * @see #isAtLeast(XInputVersion)
     * @see #requireAtLeast(XInputVersion)
     */
    public static @Nullable XInputVersion getVersion() {
        if (!isAvailable()) {
            return null;
        }

        XInputLibraryVersion version = XInputDevice.getLibraryVersion();
        switch (version) {
            case XINPUT_9_1_0:
                return XInputVersion.V1_0;
            case XINPUT_1_3:
                return XInputVersion.V1_3;
            case XINPUT_1_4:
                return XInputVersion.V1_4;
            default:
                /* this should never happen */
                String msg = "unexpected library version " + version;
                throw new XInputException(msg);
        }
    }

    /**
     * Returns if XInput is at least a given version.
     *
     * @param version the version to compare with.
     * @return {@code true} if the current version of XInput is at least
     * {@code version}, {@code false} otherwise.
     * @throws NullPointerException if {@code version} is {@code null}.
     * @see #getVersion()
     * @see #requireAtLeast(XInputVersion)
     */
    public static boolean isAtLeast(@NotNull XInputVersion version) {
        Objects.requireNonNull(version, "version cannot be null");
        XInputVersion currentVersion = getVersion();
        if (currentVersion != null) {
            return currentVersion.isAtLeast(version);
        } else {
            return false;
        }
    }

    /**
     * Requires the current version of XInput to be at least a given
     * version before continuing execution.
     *
     * @param version the minimum version of XInput this machine must
     *                have present.
     * @throws NullPointerException       if {@code version} is {@code null}.
     * @throws XInputUnavailableException if the XInput library is not
     *                                    available on this machine.
     * @throws XInputVersionException     if the current version of
     *                                    the library is not at least
     *                                    {@code version}.
     * @see #getVersion()
     * @see #isAtLeast(XInputVersion)
     */
    public static void requireAtLeast(@NotNull XInputVersion version) {
        Objects.requireNonNull(version, "version cannot be null");
        XInputVersion currentVersion = getVersion();
        if (currentVersion == null) {
            throw new XInputUnavailableException();
        } else if (!currentVersion.isAtLeast(version)) {
            throw new XInputVersionException(currentVersion, version);
        }
    }

    /**
     * Enables or disables the reporting state of XInput.
     * <p>
     * Disabling the reporting state of XInput will result in the library
     * reporting a neutral state for all controllers and no vibration data
     * being sent. This is useful for applications which can lose and then
     * regain focus.
     *
     * @param enabled {@code true} to enable XInput, {@code false} to
     *                disable it and thus receive neutral data.
     * @throws XInputUnavailableException if the XInput library is not
     *                                    available on this machine.
     * @throws XInputVersionException     if the current version of
     *                                    the library is not at least
     *                                    {@link XInputVersion#V1_4}.
     */
    public static void setEnabled(boolean enabled) {
        requireAtLeast(XInputVersion.V1_4);
        XInputDevice14.setEnabled(enabled);
    }

    /**
     * Attempts to enable or disable the reporting state of XInput.
     * <p>
     * Disabling the reporting state of XInput result in the library
     * reporting a neutral state for all controllers and no vibration
     * data being sent. This is useful for applications which can lose
     * and then regain focus.
     * <p>
     * <b>Alternative to:</b> {@link #setEnabled(boolean)}, which only
     * enables or disables the reporting state of XInput if the current
     * version of the library is at least {@link XInputVersion#V1_4}.
     *
     * @param enabled {@code true} to enable XInput, {@code false} to
     *                disable it and thus receive neutral data.
     * @return {@code true} if the reporting state of XInput was updated
     * successfully, {@code false} otherwise.
     */
    public static boolean trySetEnabled(boolean enabled) {
        if (isAtLeast(XInputVersion.V1_4)) {
            XInputDevice14.setEnabled(enabled);
            return true;
        } else {
            return false;
        }
    }

    @VisibleForTesting
    static void cacheControllers() {
        XInputDevice[] xDevices;
        try {
            if (XInputDevice14.isAvailable()) {
                xDevices = XInputDevice14.getAllDevices();
            } else {
                xDevices = XInputDevice.getAllDevices();
            }
        } catch (XInputNotLoadedException e) {
            throw new XInputSetupException(e);
        }

        for (int i = 0; i < CONTROLLERS.length; i++) {
            /* @formatter:off */
            AtomicXInputDevice xDevice =
                    new AtomicXInputDevice(xDevices[i]);
            CONTROLLERS[i] = new XboxController((c, r) ->
                    new XInputXboxAdapter(c, r, xDevice));
            /* @formatter:on */
        }
    }

    /**
     * Gets an {@link XboxController} for the given player number.
     * Take note that for a given player number, the same controller
     * instance will be returned every time this method is called.
     * <p>
     * When using XInput, there is a maximum of four players. The
     * player number should also be treated like an array index.
     * As such, {@code playerNum} starts at zero for player one.
     * <p>
     * <b>Thread safety:</b> The returned controller can be shared
     * among multiple threads. Its adapter is {@link XInputXboxAdapter},
     * which is <i>thread-safe.</i>
     *
     * @param playerNum the player number.
     * @return the controller for {@code playerNum}.
     * @throws IndexOutOfBoundsException  if {@code playerNum} is less than
     *                                    zero or greater than or equal to
     *                                    the available player count.
     * @throws XInputUnavailableException if the XInput library is not
     *                                    available on this machine.
     * @throws XInputSetupException       if a setup error occurs.
     * @see XInput#isAvailable()
     */
    public static @NotNull XboxController getPlayer(int playerNum) {
        if (playerNum < 0 || playerNum >= CONTROLLERS.length) {
            String msg = "Player Number: " + playerNum;
            msg += ", Player Count: " + CONTROLLERS.length;
            throw new IndexOutOfBoundsException(msg);
        }

        requireAvailable();

        /*
         * If the cache error has a non-null value, that means caching
         * the controllers was previously attempted but failed. Instead
         * of returning null, or attempting to load them again, simply
         * rethrow the error back to the next caller.
         */
        XInputException cacheError = CONTROLLERS_ERROR.get();
        if (cacheError != null) {
            throw cacheError;
        }

        /*
         * A lock is utilized here just in case two threads call this
         * method at the same time before the controllers are cached.
         * If this were to occur without a lock, it would be possible
         * for one of the threads to get null while the other thread
         * was attempting to load the controllers into memory.
         */
        CONTROLLERS_LOCK.lock();
        try {
            /*
             * The check is performed here so the cacheControllers()
             * method be called multiple times by unit tests without
             * it immediately returning after its first invocation.
             */
            if (CONTROLLERS_CACHED.compareAndSet(false, true)) {
                cacheControllers();
            }
            return CONTROLLERS[playerNum];
        } catch (XInputException cause) {
            CONTROLLERS_ERROR.set(cause);
            throw cause; /* don't fail silently */
        } finally {
            CONTROLLERS_LOCK.unlock();
        }
    }

    /**
     * Gets the {@link XboxController} for every available player.
     * Take note that the returned array will be populated with the
     * same controller instances every time this method is called.
     * <p>
     * The index of each controller in the array corresponds to their
     * player number. For example, the first element in the array is
     * player one. Furthermore, the caller is also free to modify the
     * returned array. A new one is generated every time this method
     * is called.
     * <p>
     * <b>Thread safety:</b> The returned controllers can be shared
     * among multiple threads. Their adapter is {@link XInputXboxAdapter},
     * which is <i>thread-safe.</i>
     *
     * @return the controllers for every available player.
     * @throws XInputUnavailableException if the XInput library is not
     *                                    available on this machine.
     * @throws XInputSetupException       if a setup error occurs.
     * @see XInput#isAvailable()
     */
    public static @NotNull XboxController @NotNull [] getAllPlayers() {
        requireAvailable();
        XboxController[] players = new XboxController[PLAYER_COUNT];
        for (int i = 0; i < players.length; i++) {
            players[i] = XInput.getPlayer(i);
        }
        return players;
    }

}
