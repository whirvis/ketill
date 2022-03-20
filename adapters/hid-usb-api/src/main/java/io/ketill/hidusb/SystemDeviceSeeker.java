package io.ketill.hidusb;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An I/O device seeker for devices attached to the system.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the device seeker must
 * be told what to seek via {@link #targetProduct(DeviceId)}. If this
 * is neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 * @param <S> the system device type.
 * @see #getDeviceId(Object)
 * @see #getDeviceHash(Object)
 */
public abstract class SystemDeviceSeeker<I extends IoDevice, S>
        extends IoDeviceSeeker<I> {

    /**
     * This minimum scan interval was chosen as values lower than this were
     * found to cause mysterious errors. Lower scan intervals such as 500ms
     * and even 250ms seemed to work, but would sometimes fail also. As such,
     * 1000ms was the value chosen for the minimum scan interval.
     */
    public static final long MINIMUM_SCAN_INTERVAL = 1000L;

    public final long scanIntervalMs;
    private long lastScanTime;

    private final Set<DeviceId> targeted;
    private final Map<Integer, BlockedDevice<S>> blocked;
    private final List<S> attached;
    private final List<S> connected;

    private @Nullable Consumer<BlockedDevice<S>> onBlockCallback;
    private @Nullable Consumer<BlockedDevice<S>> onUnblockCallback;

    /**
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less than
     *                                  {@value #MINIMUM_SCAN_INTERVAL}.
     */
    public SystemDeviceSeeker(long scanIntervalMs) {
        if (scanIntervalMs < MINIMUM_SCAN_INTERVAL) {
            String msg = "scanIntervalMs cannot be less than";
            msg += " " + MINIMUM_SCAN_INTERVAL;
            throw new IllegalArgumentException(msg);
        }

        this.scanIntervalMs = scanIntervalMs;

        this.targeted = new HashSet<>();
        this.blocked = new HashMap<>();
        this.attached = new ArrayList<>();
        this.connected = new ArrayList<>();
    }

    /**
     * Constructs a new {@code SystemDeviceSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a device scan from being performed unless
     * enough time has elapsed between method calls.
     */
    public SystemDeviceSeeker() {
        this(MINIMUM_SCAN_INTERVAL);
    }

    protected abstract @NotNull DeviceId getDeviceId(@NotNull S systemDevice);

    /**
     * This is used by the system device seeker to properly enforce device
     * blocking. If the system device type provides a proper implementation
     * of {@link Object#hashCode()}, feel free to implement this method as:
     * <pre>
     * &#64;Override
     * protected int getDeviceHash(@NotNull S systemDevice) {
     *     return systemDevice.hashCode();
     * }
     * </pre>
     * Otherwise, generate a hash with unique data for the device.
     *
     * @param systemDevice the system device.
     * @return the generated hash for {@code systemDevice}.
     */
    protected abstract int getDeviceHash(@NotNull S systemDevice);

    /**
     * @param deviceId the device ID of the product.
     * @return {@code true} if this device seeker is seeking out devices
     * with the specified device ID, {@code false} otherwise.
     * @throws NullPointerException if {@code deviceId} is {@code null}.
     * @see #targetProduct(DeviceId)
     */
    public final boolean isTargetingProduct(@NotNull DeviceId deviceId) {
        Objects.requireNonNull(deviceId, "deviceId cannot be null");
        return targeted.contains(deviceId);
    }

    /**
     * @param vendorId  the vendor ID of the device.
     * @param productId the product ID of the device.
     * @return {@code true} if this device seeker is seeking out devices
     * with the specified device ID, {@code false} otherwise.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     * @see #targetProduct(int, int)
     */
    public final boolean isTargetingProduct(int vendorId, int productId) {
        return this.isTargetingProduct(new DeviceId(vendorId, productId));
    }

    /**
     * This is a shorthand for {@link #isTargetingProduct(DeviceId)},
     * with the argument for {@code deviceId} being the device ID as
     * returned by {@link #getDeviceId(Object)}.
     *
     * @param systemDevice the system device.
     * @return {@code true} if this device seeker is seeking out
     * {@code device}, {@code false} otherwise.
     * @throws NullPointerException if {@code systemDevice} is {@code null};
     *                              if the device ID returned by
     *                              {@link #getDeviceId(Object)}
     *                              is {@code null}.
     * @see #targetProduct(DeviceId)
     */
    public final boolean isTargetingDevice(@NotNull S systemDevice) {
        Objects.requireNonNull(systemDevice,
                "systemDevice cannot be null");
        DeviceId deviceId = this.getDeviceId(systemDevice);
        Objects.requireNonNull(deviceId,
                "getDeviceId(DeviceId) cannot return null");
        return this.isTargetingProduct(deviceId);
    }

    /**
     * Indicates to the seeker it should target system devices with
     * the specified device ID. When such a device is located, the
     * {@link #onDeviceAttach(Object)} callback will be executed.
     *
     * @param deviceId the ID of the product to target.
     * @throws NullPointerException  if {@code deviceId} is {@code null}.
     * @throws IllegalStateException if this device seeker has been closed
     *                               via {@link #close()}.
     * @see #onDeviceAttach(Object)
     * @see #onDeviceConnect(Object)
     */
    protected void targetProduct(@NotNull DeviceId deviceId) {
        Objects.requireNonNull(deviceId, "deviceId cannot be null");
        this.requireOpen();
        targeted.add(deviceId);
    }

    /**
     * Indicates to the seeker it should target system devices with
     * the specified device ID. When such a device is located, the
     * {@link #onDeviceAttach(Object)} callback will be executed.
     * <p>
     * This method is a shorthand for {@link #targetProduct(DeviceId)}
     * with the argument for {@code deviceId} being constructed from the
     * arguments for {@code vendorId} and {@code productId}.
     *
     * @param vendorId  the vendor ID of the device.
     * @param productId the product ID of the device.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     * @throws IllegalStateException    if this device seeker has been closed
     *                                  via {@link #close()}.
     * @see #onDeviceAttach(Object)
     * @see #onDeviceConnect(Object)
     */
    protected final void targetProduct(int vendorId, int productId) {
        this.targetProduct(new DeviceId(vendorId, productId));
    }

    /**
     * Indicates to the seeker it should no longer seek out devices
     * with the specified device ID. All currently connected devices
     * with a matching device ID will be disconnected and then detached.
     *
     * @param deviceId the device ID of the product to drop.
     * @throws NullPointerException  if {@code deviceId} is {@code null}.
     * @throws IllegalStateException if this device seeker has been closed
     *                               via {@link #close()}.
     * @see #onDeviceDisconnect(Object)
     * @see #onDeviceDetach(Object)
     */
    protected void dropProduct(@NotNull DeviceId deviceId) {
        Objects.requireNonNull(deviceId, "deviceId cannot be null");
        this.requireOpen();
        if (!this.isTargetingProduct(deviceId)) {
            return;
        }

        targeted.remove(deviceId);

        /*
         * Disconnect all connected devices with a matching device
         * ID. This must be done before detaching them to preserve
         * the natural order of system device operations.
         */
        Iterator<S> connectedI = connected.iterator();
        while (connectedI.hasNext()) {
            S systemDevice = connectedI.next();
            DeviceId systemDeviceId = this.getDeviceId(systemDevice);
            if (deviceId.equals(systemDeviceId)) {
                connectedI.remove();
                this.disconnectDevice(systemDevice, false);
            }
        }

        Iterator<S> attachedI = attached.iterator();
        while (attachedI.hasNext()) {
            S systemDevice = attachedI.next();
            DeviceId systemDeviceId = this.getDeviceId(systemDevice);
            if (deviceId.equals(systemDeviceId)) {
                attachedI.remove();
                this.detachDevice(systemDevice, false);
            }
        }
    }

    /**
     * Indicates to the seeker it should no longer seek out devices
     * with the specified device ID. All currently connected devices
     * with a matching device ID will be disconnected and then detached.
     * <p>
     * This method is a shorthand for {@link #dropProduct(DeviceId)}
     * with the argument for {@code deviceId} being constructed from
     * the arguments for {@code vendorId} and {@code productId}.
     *
     * @param vendorId  the vendor ID of the device.
     * @param productId the product ID of the device.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     * @throws IllegalStateException    if this device seeker has been closed
     *                                  via {@link #close()}.
     * @see #onDeviceDisconnect(Object)
     * @see #onDeviceDetach(Object)
     */
    protected final void dropProduct(int vendorId, int productId) {
        this.dropProduct(new DeviceId(vendorId, productId));
    }

    /**
     * @param systemDevice the system device to check.
     * @return {@code true} if {@code systemDevice} is currently
     * blocked, {@code false} otherwise.
     * @throws NullPointerException if {@code systemDevice} is {@code null}.
     * @see #blockDevice(Object, boolean)
     */
    protected final boolean isBlocked(@NotNull S systemDevice) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        int deviceHash = this.getDeviceHash(systemDevice);
        return blocked.containsKey(deviceHash);
    }

    /**
     * When blocked, a device is automatically disconnected (assuming it was
     * connected beforehand.) Afterwards, it will not be able to re-attach
     * after detaching (unless {@code unblockAfterDetach} is {@code true}.)
     * <p>
     * <b>Note:</b> Unless {@code unblockAfterDetach} is {@code false}, this
     * will <i>not</i> detach the device. Doing so would result in the device
     * being unblocked immediately after calling this method.
     *
     * @param systemDevice       the system device to block.
     * @param cause              the reason for blocking. A value of
     *                           {@code null} is permitted, and indicates
     *                           that the cause is unknown.
     * @param unblockAfterDetach {@code true} if {@code systemDevice}
     *                           should be unblocked after being
     *                           detached, {@code false} otherwise.
     * @throws NullPointerException  if {@code systemDevice} is {@code null}.
     * @throws IllegalStateException if this device seeker has been closed
     *                               via {@link #close()};
     *                               if {@code systemDevice} has already
     *                               been blocked.
     * @see #unblockDevice(Object)
     * @see #onBlockDevice(Consumer)
     */
    protected void blockDevice(@NotNull S systemDevice,
                               @Nullable Throwable cause,
                               boolean unblockAfterDetach) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        this.requireOpen();
        if (this.isBlocked(systemDevice)) {
            throw new IllegalStateException("device already blocked");
        }

        BlockedDevice<S> block = new BlockedDevice<>(systemDevice, cause,
                unblockAfterDetach);
        int deviceHash = this.getDeviceHash(systemDevice);
        blocked.put(deviceHash, block);

        /*
         * Disconnect the device if it is currently connected.
         * It would not make sense for it to linger if it has
         * been blocked. Do this before detaching it from the
         * device seeker to preserve natural order.
         */
        this.disconnectDevice(systemDevice, true);

        /*
         * Only detach the device if it will not be unblocked
         * afterwards. If the device is marked to be unblocked
         * when it is detached, detaching it now would result
         * in it being re-attached on the next scan.
         */
        if (!unblockAfterDetach) {
            this.detachDevice(systemDevice, true);
        }

        /* execute callback after consequences */
        if (onBlockCallback != null) {
            onBlockCallback.accept(block);
        }
    }

    /**
     * When blocked, a device is automatically disconnected (assuming it was
     * connected beforehand.) Afterwards, it will not be able to re-attach
     * after detaching (unless {@code unblockAfterDetach} is {@code true}.)
     * <p>
     * <b>Note:</b> Unless {@code unblockAfterDetach} is {@code false}, this
     * will <i>not</i> detach the device. Doing so would result in the device
     * being unblocked immediately after calling this method.
     * <p>
     * This method is a shorthand for
     * {@link #blockDevice(Object, Throwable, boolean)}, with the
     * argument for {@code cause} being {@code null}.
     *
     * @param systemDevice       the system device to block.
     * @param unblockAfterDetach {@code true} if {@code systemDevice}
     *                           should be unblocked after being
     *                           detached, {@code false} otherwise.
     * @throws NullPointerException  if {@code systemDevice} is {@code null}.
     * @throws IllegalStateException if this device seeker has been closed
     *                               via {@link #close()};
     *                               if {@code systemDevice} has already
     *                               been blocked.
     * @see #unblockDevice(Object)
     * @see #onBlockDevice(Consumer)
     */
    protected final void blockDevice(@NotNull S systemDevice,
                                     boolean unblockAfterDetach) {
        this.blockDevice(systemDevice, null, unblockAfterDetach);
    }

    /**
     * <b>Note:</b> This method <i>does not</i> prevent a device from
     * being blocked again. To change the behavior of blocking, override
     * {@link #blockDevice(Object, boolean)}.
     *
     * @param systemDevice the system device to unblock.
     * @throws NullPointerException  if {@code systemDevice} is {@code null}.
     * @throws IllegalStateException if this device seeker has been closed
     *                               via {@link #close()}.
     * @see #onUnblockDevice(Consumer)
     */
    protected void unblockDevice(@NotNull S systemDevice) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        this.requireOpen();

        int deviceHash = this.getDeviceHash(systemDevice);
        BlockedDevice<S> block = blocked.remove(deviceHash);
        if (block != null && onUnblockCallback != null) {
            onUnblockCallback.accept(block);
        }
    }

    /**
     * Sets the callback for when a device is blocked. If this callback
     * was set <i>after</i> one or more devices have been blocked, it will
     * not be called for them.
     *
     * @param callback the code to execute when a device is blocked. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #blockDevice(Object, boolean)
     */
    public final void onBlockDevice(@Nullable Consumer<BlockedDevice<S>> callback) {
        this.onBlockCallback = callback;
    }

    /**
     * Sets the callback for when a device is unblocked. If this callback
     * was set <i>after</i> one or more devices have been unblocked, it will
     * not be called for them.
     *
     * @param callback the code to execute when a device is unblocked. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @see #unblockDevice(Object)
     */
    public final void onUnblockDevice(@Nullable Consumer<BlockedDevice<S>> callback) {
        this.onUnblockCallback = callback;
    }

    /**
     * Called periodically by {@link #seekImpl()} per the scan interval
     * specified during construction. This method should return <i>all</i>
     * devices currently connected to the system. Devices will be filtered
     * out before being attached using their device ID.
     *
     * @return all devices currently connected to the system.
     * @see #isTargetingProduct(DeviceId)
     * @see #onDeviceAttach(Object)
     * @see #onDeviceDetach(Object)
     */
    protected abstract @NotNull Collection<@NotNull S> scanDevices();

    /**
     * @param systemDevice the system device to check.
     * @return {@code true} if {@code systemDevice} is attached,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code systemDevice} is {@code null}.
     */
    protected final boolean isAttached(@NotNull S systemDevice) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        return attached.contains(systemDevice);
    }

    private void attachDevice(S systemDevice) {
        if (this.isAttached(systemDevice)) {
            return;
        } else if (this.isBlocked(systemDevice)) {
            return;
        }

        if (this.isTargetingDevice(systemDevice)) {
            attached.add(systemDevice);
            try {
                this.onDeviceAttach(systemDevice);
            } catch (Throwable cause) {
                this.blockDevice(systemDevice, cause, true);
            }
        }
    }

    private void detachDevice(S systemDevice, boolean onlyIfAttached) {
        if (onlyIfAttached && !attached.contains(systemDevice)) {
            return; /* not attached when required to be */
        }

        int deviceHash = this.getDeviceHash(systemDevice);
        BlockedDevice<S> listing = blocked.get(deviceHash);
        if (listing != null && listing.unblockAfterDetach) {
            blocked.remove(deviceHash);
        }

        attached.remove(systemDevice);

        try {
            this.onDeviceDetach(systemDevice);
        } catch (Throwable cause) {
            this.blockDevice(systemDevice, cause, false);
        }
    }

    /**
     * Called when a system device being sought after has been attached.
     * <p>
     * By default, this method just connects the device. When overridden,
     * it should perform device setup before connecting it via
     * {@link #connectDevice(Object)}.
     * <p>
     * <b>Note:</b> If any errors occur in this method, the device will
     * not be connected. It will also be blocked until it is later
     * detached using {@link #blockDevice(Object, boolean)}.
     *
     * @param systemDevice the attached system device.
     * @throws Exception if an error occurs.
     * @see #onDeviceConnect(Object)
     */
    @SuppressWarnings("RedundantThrows")
    protected void onDeviceAttach(@NotNull S systemDevice) throws Exception {
        /* optional implement, just connect the device */
        this.connectDevice(systemDevice);
    }

    /**
     * Called when an attached system device has been detached.
     * <p>
     * By default, this method just disconnects the device. When overridden,
     * it should perform device shutdown before disconnecting it via
     * {@link #disconnectDevice(Object, boolean)}. <b>Take note</b> that
     * devices can be detached without ever being disconnected (e.g., if
     * an error occurs in {@link #onDeviceAttach(Object)} so the device
     * never got connected).
     * <p>
     * <b>Note:</b> If any errors occur in this method, the device will be
     * blocked permanently using {@link #blockDevice(Object, boolean)},
     * meaning it won't be able to re-attach.
     *
     * @param systemDevice the detached system device.
     * @throws Exception if an error occurs.
     * @see #onDeviceDisconnect(Object)
     */
    @SuppressWarnings("RedundantThrows")
    protected void onDeviceDetach(@NotNull S systemDevice) throws Exception {
        /* optional implement, just disconnect the device */
        this.disconnectDevice(systemDevice, true);
    }

    /**
     * @param systemDevice the system device to check.
     * @return {@code true} if {@code systemDevice} is connected,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code systemDevice} is {@code null}.
     */
    protected final boolean isConnected(@NotNull S systemDevice) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        return connected.contains(systemDevice);
    }

    /**
     * Connects a device to this device seeker. If {@code systemDevice}
     * is already connected then this method has no effect.
     *
     * @param systemDevice the device to connect.
     * @throws NullPointerException if {@code systemDevice} is {@code null}.
     * @see #onDeviceAttach(Object)
     */
    protected void connectDevice(@NotNull S systemDevice) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        if (!this.isConnected(systemDevice)) {
            connected.add(systemDevice);
            this.onDeviceConnect(systemDevice);
        }
    }

    /**
     * Disconnects a device from this device seeker. If {@code systemDevice}
     * is not connected and {@code onlyIfConnected} is {@code false} then
     * this method has no effect.
     *
     * @param systemDevice    the device to disconnect.
     * @param onlyIfConnected {@code true} if {@code systemDevice} must be
     *                        previously connected in order for it to be
     *                        disconnected, {@code false} otherwise.
     * @throws NullPointerException if {@code systemDevice} is {@code null}.
     * @see #onDeviceDetach(Object)
     */
    protected void disconnectDevice(@NotNull S systemDevice,
                                    boolean onlyIfConnected) {
        Objects.requireNonNull(systemDevice, "systemDevice cannot be null");
        if (onlyIfConnected && !this.isConnected(systemDevice)) {
            return; /* not connected when required to be */
        }
        connected.remove(systemDevice);
        this.onDeviceDisconnect(systemDevice);
    }

    /**
     * Called when a device being sought after has connected.
     * <p>
     * <b>Note:</b> Connected devices are <i>not</i> discovered.
     * They must be discovered using {@link #discoverDevice(IoDevice)}.
     *
     * @param systemDevice the system device.
     */
    protected abstract void onDeviceConnect(@NotNull S systemDevice);

    /**
     * Called when a previously connected device has disconnected.
     * <p>
     * <b>Note:</b> Disconnected devices are <i>not</i> forgotten.
     * They must be forgotten using {@link #forgetDevice(IoDevice)}.
     *
     * @param systemDevice the system device.
     */
    protected abstract void onDeviceDisconnect(@NotNull S systemDevice);

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() throws Exception {
        if (targeted.isEmpty()) {
            throw new IllegalStateException("no products targeted");
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime >= scanIntervalMs) {
            Collection<S> scanned = this.scanDevices();

            Iterator<S> attachedI = attached.iterator();
            while (attachedI.hasNext()) {
                S systemDevice = attachedI.next();
                if (!scanned.contains(systemDevice)) {
                    attachedI.remove();
                    this.detachDevice(systemDevice, false);
                }
            }

            for (S systemDevice : scanned) {
                this.attachDevice(systemDevice);
            }

            this.lastScanTime = currentTime;
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void close() {
        if (this.isClosed()) {
            return;
        }

        targeted.clear();
        blocked.clear();

        super.close();
    }

}
