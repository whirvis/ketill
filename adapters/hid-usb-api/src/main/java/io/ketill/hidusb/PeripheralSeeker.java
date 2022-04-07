package io.ketill.hidusb;

import io.ketill.IoDevice;
import io.ketill.IoDeviceSeeker;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * An I/O device seeker for peripherals attached to the system.<br>
 * <b>For the sake of the reader:</b> The term "device" or similar refers
 * to an {@link IoDevice}. The term "peripheral" or similar refers a device,
 * virtual or physical, attached to the system.
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the peripheral seeker
 * must be told what to seek via {@link #targetProduct(ProductId)}. If
 * this is neglected, an {@code IllegalStateException} will be thrown.
 *
 * @param <I> the I/O device type.
 * @param <P> the peripheral type.
 * @see #getId(Object)
 * @see #getHash(Object)
 */
public abstract class PeripheralSeeker<I extends IoDevice, P>
        extends IoDeviceSeeker<I> {

    /**
     * Package-private field to disable the required wait time before a
     * peripheral scan is performed in {@link #seek()}. This exists only
     * for testing. This should never be {@code true} in production!
     */
    static boolean scanWaitDisabled = false;

    /**
     * This minimum scan interval was chosen as values lower than this were
     * found to cause mysterious errors. Lower scan intervals such as 500ms
     * and even 250ms seemed to work, but would sometimes fail also. As such,
     * 1000ms was the value chosen for the minimum scan interval.
     */
    public static final long MINIMUM_SCAN_INTERVAL = 1000L;

    public final long scanIntervalMs;
    private long lastScanTime;

    private final List<ProductId> targeted;
    private final Map<Integer, BlockedPeripheral<P>> blocked;
    private final List<P> attached;
    private final List<P> connected;

    /* @formatter:off */
    private @Nullable BiConsumer<PeripheralSeeker<I, P>,
            ProductId> targetCallback;
    private @Nullable BiConsumer<PeripheralSeeker<I, P>,
            ProductId> dropCallback;
    private @Nullable BiConsumer<PeripheralSeeker<I, P>,
            BlockedPeripheral<P>> blockCallback;
    private @Nullable BiConsumer<PeripheralSeeker<I, P>,
            BlockedPeripheral<P>> unblockCallback;
    /* @formatter:on */

    /**
     * @param scanIntervalMs the interval in milliseconds between peripheral
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       scan from being performed unless enough time
     *                       has elapsed between method calls.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less than
     *                                  {@value #MINIMUM_SCAN_INTERVAL}.
     */
    public PeripheralSeeker(long scanIntervalMs) {
        if (scanIntervalMs < MINIMUM_SCAN_INTERVAL) {
            String msg = "scanIntervalMs cannot be less than";
            msg += " " + MINIMUM_SCAN_INTERVAL;
            throw new IllegalArgumentException(msg);
        }

        this.scanIntervalMs = scanIntervalMs;

        this.targeted = new ArrayList<>();
        this.blocked = new HashMap<>();
        this.attached = new ArrayList<>();
        this.connected = new ArrayList<>();
    }

    /**
     * Constructs a new {@code PeripheralSeeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a peripheral scan from being performed
     * unless enough time has elapsed between method calls.
     */
    public PeripheralSeeker() {
        this(MINIMUM_SCAN_INTERVAL);
    }

    protected abstract @NotNull ProductId getId(@NotNull P peripheral);

    /**
     * Used by the peripheral seeker to properly enforce peripheral blocking.
     * If the peripheral type provides a proper implementation of
     * {@link Object#hashCode()}, feel free to implement this method as:
     * <pre>
     * &#64;Override
     * protected int getHash(@NotNull P peripheral) {
     *     return peripheral.hashCode();
     * }
     * </pre>
     * Otherwise, generate a hash with unique data for the peripheral.
     *
     * @param peripheral the peripheral.
     * @return the generated hash for {@code peripheral}.
     */
    protected abstract int getHash(@NotNull P peripheral);

    /**
     * Sets the callback for when a product is targeted. If this callback
     * was set <i>after</i> one or more products have been targeted, it
     * will not be called for them.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event
     * should override {@link #productTargeted(ProductId)}. The callback
     * is for users.
     *
     * @param callback the code to execute when a product is targeted. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #targetProduct(ProductId)
     */
    /* @formatter:off */
    public final void
            onTargetProduct(@Nullable BiConsumer<PeripheralSeeker<I, P>,
                            ProductId> callback) {
        this.requireOpen();
        this.targetCallback = callback;
    }
    /* @formatter:on */

    /**
     * Sets the callback for when a product is dropped. If this callback
     * was set <i>after</i> one or more products have been dropped, it
     * will not be called for them.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event
     * should override {@link #productDropped(ProductId)}. The callback
     * is for users.
     *
     * @param callback the code to execute when a product is dropped. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #dropProduct(ProductId)
     */
    /* @formatter:off */
    public final void
            onDropProduct(@Nullable BiConsumer<PeripheralSeeker<I, P>,
                          ProductId> callback) {
        this.requireOpen();
        this.dropCallback = callback;
    }
    /* @formatter:on */

    /**
     * Sets the callback for when a peripheral is blocked. If this callback
     * was set <i>after</i> one or more peripherals have been blocked, it
     * will not be called for them.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #peripheralBlocked(BlockedPeripheral)}. The callback
     * is for users.
     *
     * @param callback the code to execute when a peripheral is blocked. A
     *                 value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #blockPeripheral(Object, boolean)
     */
    /* @formatter:off */
    public final void
            onBlockPeripheral(@Nullable BiConsumer<PeripheralSeeker<I, P>,
                              BlockedPeripheral<P>> callback) {
        this.requireOpen();
        this.blockCallback = callback;
    }
    /* @formatter:on */

    /**
     * Sets the callback for when a peripheral is unblocked. If this callback
     * was set <i>after</i> one or more peripherals have been unblocked, it
     * will not be called for them.
     * <p>
     * <b>Note:</b> Extending classes wishing to listen for this event should
     * override {@link #peripheralUnblocked(BlockedPeripheral)}. The callback
     * is for users.
     *
     * @param callback the code to execute when a peripheral is unblocked.
     *                 A value of {@code null} is permitted, and will result
     *                 in nothing being executed.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #unblockPeripheral(Object)
     */
    /* @formatter:off */
    public final void
            onUnblockPeripheral(@Nullable BiConsumer<PeripheralSeeker<I, P>,
                                BlockedPeripheral<P>> callback) {
        this.requireOpen();
        this.unblockCallback = callback;
    }
    /* @formatter:on */

    /**
     * @param id the ID of the peripheral.
     * @return {@code true} if this peripheral seeker is seeking out
     * peripherals the specified ID, {@code false} otherwise.
     * @throws NullPointerException if {@code id} is {@code null}.
     * @see #targetProduct(ProductId)
     */
    public final boolean isTargetingProduct(@NotNull ProductId id) {
        Objects.requireNonNull(id, "id cannot be null");
        return targeted.contains(id);
    }

    /**
     * @param vendorId  the vendor ID of the peripheral.
     * @param productId the product ID of the peripheral.
     * @return {@code true} if this peripheral seeker is seeking out
     * peripherals with the specified ID, {@code false} otherwise.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     * @see #targetProduct(int, int)
     */
    public final boolean isTargetingProduct(int vendorId, int productId) {
        return this.isTargetingProduct(new ProductId(vendorId, productId));
    }

    /**
     * This is a shorthand for {@link #isTargetingProduct(ProductId)},
     * with the argument for {@code id} being the product ID as returned
     * by {@link #getId(Object)}.
     *
     * @param peripheral the peripheral.
     * @return {@code true} if this peripheral seeker is seeking out
     * {@code peripheral}, {@code false} otherwise.
     * @throws NullPointerException if {@code peripheral} is {@code null};
     *                              if the product ID returned by
     *                              {@link #getId(Object)} is {@code null}.
     * @see #targetProduct(ProductId)
     */
    public final boolean isTargetingPeripheral(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral,
                "peripheral cannot be null");
        ProductId peripheralId = this.getId(peripheral);
        Objects.requireNonNull(peripheralId,
                "getId(ProductId) cannot return null");
        return this.isTargetingProduct(peripheralId);
    }

    /**
     * Indicates to the seeker it should target peripherals with the
     * specified product ID. When such a peripheral is located, the
     * {@link #peripheralAttached(Object)} callback will be executed.
     *
     * @param id the ID of the product to target.
     * @throws NullPointerException  if {@code id} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #peripheralAttached(Object)
     * @see #peripheralConnected(Object)
     */
    protected void targetProduct(@NotNull ProductId id) {
        Objects.requireNonNull(id, "iId cannot be null");
        this.requireOpen();

        if (!targeted.contains(id)) {
            targeted.add(id);
            this.productTargeted(id);
            if (targetCallback != null) {
                targetCallback.accept(this, id);
            }
        }
    }

    /**
     * Indicates to the seeker it should target peripherals with the
     * specified product ID. When such a peripheral is located, the
     * {@link #peripheralAttached(Object)} callback will be executed.
     * <p>
     * This method is a shorthand for {@link #targetProduct(ProductId)}
     * with the argument for {@code id} being constructed from the
     * arguments for {@code vendorId} and {@code productId}.
     *
     * @param vendorId  the vendor ID of the peripheral.
     * @param productId the product ID of the peripheral.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     * @throws IllegalStateException    if this peripheral seeker has been
     *                                  closed via {@link #close()}.
     * @see #peripheralAttached(Object)
     * @see #peripheralConnected(Object)
     */
    protected final void targetProduct(int vendorId, int productId) {
        this.targetProduct(new ProductId(vendorId, productId));
    }

    /**
     * Called when a product is targeted. Overriding this method allows
     * for a peripheral seeker to know when a product is targeted without
     * needing to set themselves as the callback.
     *
     * @param id the targeted product ID.
     */
    protected void productTargeted(@NotNull ProductId id) {
        /* optional implement */
    }

    /**
     * Indicates to the seeker it should no longer seek out peripherals
     * with the specified peripherals ID. Peripherals currently connected
     * with a matching product ID will be disconnected and then detached.
     *
     * @param id the ID of the product to drop.
     * @throws NullPointerException  if {@code id} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #peripheralDisconnected(Object)
     * @see #peripheralDetached(Object)
     */
    protected void dropProduct(@NotNull ProductId id) {
        Objects.requireNonNull(id, "id cannot be null");
        this.requireOpen();
        if (!this.isTargetingProduct(id)) {
            return;
        }

        targeted.remove(id);

        /*
         * Disconnect all connected peripherals with a matching
         * product ID. This must be done before detaching them to
         * preserve the natural order of peripheral operations.
         */
        Iterator<P> connectedI = connected.iterator();
        while (connectedI.hasNext()) {
            P peripheral = connectedI.next();
            ProductId productId = this.getId(peripheral);
            if (id.equals(productId)) {
                connectedI.remove();
                this.disconnectPeripheral(peripheral, false);
            }
        }

        Iterator<P> attachedI = attached.iterator();
        while (attachedI.hasNext()) {
            P peripheral = attachedI.next();
            ProductId productId = this.getId(peripheral);
            if (id.equals(productId)) {
                attachedI.remove();
                this.detachPeripheral(peripheral, false);
            }
        }

        /* execute callback after consequences */
        this.productDropped(id);
        if (dropCallback != null) {
            dropCallback.accept(this, id);
        }
    }

    /**
     * Indicates to the seeker it should no longer seek out peripherals
     * with the specified product ID. All currently connected peripherals
     * with a matching product ID will be disconnected and then detached.
     * <p>
     * This method is a shorthand for {@link #dropProduct(ProductId)} with
     * the argument for {@code id} being constructed from the arguments for
     * {@code vendorId} and {@code productId}.
     *
     * @param vendorId  the vendor ID of the peripheral.
     * @param productId the product ID of the peripheral.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     * @throws IllegalStateException    if this peripheral seeker has been
     *                                  closed via {@link #close()}.
     * @see #peripheralDisconnected(Object)
     * @see #peripheralDetached(Object)
     */
    protected final void dropProduct(int vendorId, int productId) {
        this.dropProduct(new ProductId(vendorId, productId));
    }

    /**
     * Called when a product is dropped. Overriding this method allows
     * for a peripheral seeker to know when a product is dropped without
     * needing to set themselves as the callback.
     *
     * @param id the dropped product ID.
     */
    protected void productDropped(@NotNull ProductId id) {
        /* optional implement */
    }

    /**
     * @param peripheral the peripheral to check.
     * @return {@code true} if {@code peripheral} is currently blocked,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code peripheral} is {@code null}.
     * @see #blockPeripheral(Object, boolean)
     */
    protected final boolean isPeripheralBlocked(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        int peripheralHash = this.getHash(peripheral);
        return blocked.containsKey(peripheralHash);
    }

    /**
     * When blocked, a peripheral is automatically disconnected (assuming it
     * was already connected.) Afterwards, it will not be able to re-attach
     * after detaching (unless {@code unblockAfterDetach} is {@code true}.)
     * <p>
     * <b>Note:</b> Unless {@code unblockAfterDetach} is {@code false}, this
     * will <i>not</i> detach the peripheral. Doing so would result in the
     * peripheral being unblocked immediately after calling this method.
     *
     * @param peripheral         the peripheral to block.
     * @param cause              the reason for blocking. A value of
     *                           {@code null} is permitted, and indicates
     *                           that the cause is unknown.
     * @param unblockAfterDetach {@code true} if {@code peripheral}
     *                           should be unblocked after being detached,
     *                           {@code false} otherwise.
     * @throws NullPointerException  if {@code peripheral} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()};
     *                               if {@code peripheral} has already
     *                               been blocked.
     * @see #unblockPeripheral(Object)
     * @see #onBlockPeripheral(BiConsumer)
     */
    protected void blockPeripheral(@NotNull P peripheral,
                                   @Nullable Throwable cause,
                                   boolean unblockAfterDetach) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        this.requireOpen();
        if (this.isPeripheralBlocked(peripheral)) {
            throw new IllegalStateException("peripheral already blocked");
        }

        BlockedPeripheral<P> block = new BlockedPeripheral<>(peripheral,
                cause, unblockAfterDetach);
        int peripheralHash = this.getHash(peripheral);
        blocked.put(peripheralHash, block);

        /*
         * Disconnect the peripheral if currently connected.
         * It would not make sense for it to linger if it has
         * been blocked. Do this before detaching it from the
         * peripheral seeker to preserve natural order.
         */
        this.disconnectPeripheral(peripheral, true);

        /*
         * Only detach the peripheral if it won't be unblocked
         * afterwards. If the peripheral is set to be unblocked
         * when it is detached, detaching it now would result
         * in it being re-attached on the next scan.
         */
        if (!unblockAfterDetach) {
            this.detachPeripheral(peripheral, true);
        }

        /* execute callback after consequences */
        this.peripheralBlocked(block);
        if (blockCallback != null) {
            blockCallback.accept(this, block);
        }
    }

    /**
     * When blocked, a peripheral is automatically disconnected (assuming it
     * was already connected.) Afterwards, it will not be able to re-attach
     * after detaching (unless {@code unblockAfterDetach} is {@code true}.)
     * <p>
     * <b>Note:</b> Unless {@code unblockAfterDetach} is {@code false}, this
     * will <i>not</i> detach the peripheral. Doing so would result in the
     * peripheral being unblocked immediately after calling this method.
     * <p>
     * This method is a shorthand for
     * {@link #blockPeripheral(Object, Throwable, boolean)}, with the
     * argument for {@code cause} being {@code null}.
     *
     * @param peripheral         the peripheral to block.
     * @param unblockAfterDetach {@code true} if {@code peripheral}
     *                           should be unblocked after being detached,
     *                           {@code false} otherwise.
     * @throws NullPointerException  if {@code peripheral} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()};
     *                               if {@code peripheral} has already
     *                               been blocked.
     * @see #unblockPeripheral(Object)
     * @see #onBlockPeripheral(BiConsumer)
     */
    protected final void blockPeripheral(@NotNull P peripheral,
                                         boolean unblockAfterDetach) {
        this.blockPeripheral(peripheral, null, unblockAfterDetach);
    }

    /**
     * Called when a peripheral is blocked. Overriding this method allows
     * for a peripheral seeker to know when a peripheral is blocked without
     * needing to set themselves as the callback.
     *
     * @param block the peripheral blocking.
     */
    protected void peripheralBlocked(@NotNull BlockedPeripheral<P> block) {
        /* optional implement */
    }

    /**
     * <b>Note:</b> This method <i>does not</i> prevent a peripheral from
     * being blocked again. To change the behavior of blocking, override
     * {@link #blockPeripheral(Object, boolean)}.
     *
     * @param peripheral the peripheral to unblock.
     * @throws NullPointerException  if {@code peripheral} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #onUnblockPeripheral(BiConsumer)
     */
    protected void unblockPeripheral(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        this.requireOpen();

        int peripheralHash = this.getHash(peripheral);
        BlockedPeripheral<P> block = blocked.remove(peripheralHash);
        if (block != null) {
            this.peripheralUnblocked(block);
            if (unblockCallback != null) {
                unblockCallback.accept(this, block);
            }
        }
    }

    /**
     * Called when a peripheral is unblocked. Overriding this method
     * allows for a peripheral seeker to know when a peripheral is
     * unblocked without needing to set themselves as the callback.
     *
     * @param block the peripheral blocking.
     */
    protected void peripheralUnblocked(@NotNull BlockedPeripheral<P> block) {
        /* optional implement */
    }

    /**
     * Called periodically by {@link #seekImpl()} per the scan interval
     * specified during construction. This method should return <i>all</i>
     * peripherals currently connected to the system. Peripheral will be
     * filtered out before being attached using their product ID.
     *
     * @return all peripherals currently connected to the system.
     * @see #isTargetingProduct(ProductId)
     * @see #peripheralAttached(Object)
     * @see #peripheralDetached(Object)
     */
    protected abstract @NotNull Collection<@NotNull P> scanPeripherals();

    /**
     * @param peripheral the peripheral to check.
     * @return {@code true} if {@code peripheral} is attached,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code peripheral} is {@code null}.
     */
    protected final boolean isPeripheralAttached(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        return attached.contains(peripheral);
    }

    private void attachPeripheral(P peripheral) {
        if (!this.isTargetingPeripheral(peripheral)) {
            return;
        } else if (this.isPeripheralAttached(peripheral)) {
            return;
        } else if (this.isPeripheralBlocked(peripheral)) {
            return;
        }

        attached.add(peripheral);
        try {
            this.peripheralAttached(peripheral);
        } catch (Throwable cause) {
            this.blockPeripheral(peripheral, cause, true);
            this.peripheralAttachFailed(peripheral, cause);
        }
    }

    /**
     * Called when a targeted peripheral has been attached.
     * <p>
     * By default, this method just connects the peripheral. When
     * overridden, it should perform setup before connecting it via
     * {@link #connectPeripheral(Object)}.
     * <p>
     * <b>Note:</b> If any errors occur in this method, the peripheral
     * will not be connected. It will also be blocked until it is later
     * detached using {@link #blockPeripheral(Object, boolean)}.
     *
     * @param peripheral the attached peripheral.
     * @throws Exception if an error occurs.
     * @see #peripheralAttachFailed(Object, Throwable)
     * @see #peripheralConnected(Object)
     */
    @SuppressWarnings("RedundantThrows")
    protected void peripheralAttached(@NotNull P peripheral) throws Exception {
        /* optional implement, just connect the peripheral */
        this.connectPeripheral(peripheral);
    }

    /**
     * Called when a peripheral fails to attach. Overriding this method
     * allows for a peripheral seeker to know when a peripheral fails to
     * attach without needing to set themselves as the callback.
     *
     * @param peripheral the peripheral.
     * @param cause      the cause for failure.
     */
    protected void peripheralAttachFailed(@NotNull P peripheral,
                                          @NotNull Throwable cause) {
        /* optional implement */
    }

    private void detachPeripheral(P peripheral, boolean onlyIfAttached) {
        if (onlyIfAttached && !attached.contains(peripheral)) {
            return; /* not attached when required to be */
        }

        int peripheralHash = this.getHash(peripheral);
        BlockedPeripheral<P> listing = blocked.get(peripheralHash);
        if (listing != null && listing.unblockAfterDetach) {
            this.unblockPeripheral(peripheral);
        }

        attached.remove(peripheral);

        try {
            this.peripheralDetached(peripheral);
        } catch (Throwable cause) {
            /*
             * Only notify the peripheral seeker an error has occurred when
             * detaching a peripheral. Do not block it! That would resolve
             * nothing, since communication is already over. If the user
             * desires, they can block the peripheral themselves.
             */
            this.peripheralDetachFailed(peripheral, cause);
        }
    }

    /**
     * Called when a peripheral fails to properly detach. Overriding this
     * method allows for a peripheral seeker to know when a peripheral
     * fails to detach without needing to set themselves as the callback.
     *
     * @param peripheral the peripheral.
     * @param cause      the cause for failure.
     */
    protected void peripheralDetachFailed(@NotNull P peripheral,
                                          @NotNull Throwable cause) {
        /* optional implement */
    }

    /**
     * Called when an attached peripheral has been detached.
     * <p>
     * By default, this method just disconnects the peripheral. When
     * overridden, it should perform shutdown before disconnecting it
     * via {@link #disconnectPeripheral(Object, boolean)}.
     * <p>
     * Peripherals can be detached without ever being disconnected. For
     * example, if an error occurs in {@link #peripheralAttached(Object)}
     * meaning the peripheral never got connected.
     * <p>
     * <b>Note:</b> If any errors occur in this method, the peripheral will
     * be blocked permanently via {@link #blockPeripheral(Object, boolean)},
     * meaning it won't be able to re-attach.
     *
     * @param peripheral the detached peripheral.
     * @throws Exception if an error occurs.
     * @see #peripheralDetachFailed(Object, Throwable)
     * @see #peripheralDisconnected(Object)
     */
    @SuppressWarnings("RedundantThrows")
    protected void peripheralDetached(@NotNull P peripheral) throws Exception {
        /* optional implement, just disconnect the peripheral */
        this.disconnectPeripheral(peripheral, true);
    }

    /**
     * @param peripheral the peripheral to check.
     * @return {@code true} if {@code peripheral} is connected,
     * {@code false} otherwise.
     * @throws NullPointerException if {@code peripheral} is {@code null}.
     */
    protected final boolean isPeripheralConnected(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        return connected.contains(peripheral);
    }

    /**
     * @param peripheral the peripheral to connect.
     * @throws NullPointerException  if {@code peripheral} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()};
     *                               if {@code peripheral} is not attached;
     *                               if {@code peripheral} is already
     *                               connected.
     * @see #peripheralAttached(Object)
     */
    protected void connectPeripheral(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        this.requireOpen();
        if (!this.isPeripheralAttached(peripheral)) {
            throw new IllegalStateException("peripheral not attached");
        } else if (this.isPeripheralConnected(peripheral)) {
            throw new IllegalStateException("peripheral already connected");
        }
        connected.add(peripheral);
        this.peripheralConnected(peripheral);
    }

    /**
     * Called when a targeted peripheral has connected.
     * <p>
     * <b>Note:</b> Connected peripherals are <i>not</i> discovered.
     * They must be discovered using {@link #discoverDevice(IoDevice)}.
     *
     * @param peripheral the peripheral.
     */
    protected abstract void peripheralConnected(@NotNull P peripheral);

    /**
     * @param peripheral      the peripheral to disconnect.
     * @param onlyIfConnected {@code true} if {@code peripheral} must be
     *                        previously connected in order for it to be
     *                        disconnected, {@code false} otherwise.
     * @see #peripheralDetached(Object)
     */
    private void disconnectPeripheral(@NotNull P peripheral,
                                      boolean onlyIfConnected) {
        if (onlyIfConnected && !this.isPeripheralConnected(peripheral)) {
            return; /* not connected when required to be */
        }
        connected.remove(peripheral);
        this.peripheralDisconnected(peripheral);
    }

    /**
     * @param peripheral the peripheral to disconnect.
     * @throws NullPointerException  if {@code peripheral} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()};
     *                               if {@code peripheral} is currently
     *                               not connected.
     * @see #peripheralDetached(Object)
     */
    protected void disconnectPeripheral(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        this.requireOpen();
        if (!this.isPeripheralConnected(peripheral)) {
            throw new IllegalStateException("peripheral not connected");
        }
        this.disconnectPeripheral(peripheral, true);
    }

    /**
     * Called when a previously connected peripheral has disconnected.
     * <p>
     * <b>Note:</b> Disconnected peripherals are <i>not</i> forgotten.
     * They must be forgotten using {@link #forgetDevice(IoDevice)}.
     *
     * @param peripheral the peripheral.
     */
    protected abstract void peripheralDisconnected(@NotNull P peripheral);

    @Override
    @MustBeInvokedByOverriders
    protected void seekImpl() throws Exception {
        if (targeted.isEmpty()) {
            throw new IllegalStateException("no products targeted");
        }

        long currentTime = System.currentTimeMillis();
        long scanDelta = currentTime - lastScanTime;
        if (scanWaitDisabled || scanDelta >= scanIntervalMs) {
            Collection<P> scanned = this.scanPeripherals();

            Iterator<P> attachedI = attached.iterator();
            while (attachedI.hasNext()) {
                P peripheral = attachedI.next();
                if (!scanned.contains(peripheral)) {
                    attachedI.remove();
                    this.detachPeripheral(peripheral, false);
                }
            }

            for (P peripheral : scanned) {
                this.attachPeripheral(peripheral);
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

        /*
         * The dropProduct() checks if the argument is currently targeted
         * before actually dropping it. This gets the first element from
         * the internal list, drops it, and repeats until empty. This is
         * done to avoid ConcurrentModificationException that would occur
         * if using an enhanced for loop.
         */
        while (!targeted.isEmpty()) {
            ProductId id = targeted.get(0);
            this.dropProduct(id);
        }

        /*
         * The unblockPeripheral() method checks if the argument is
         * currently blocked before unblocking it. This gets the first
         * value from the internal map, unblocks it, and repeats until
         * empty. This avoids a ConcurrentModificationException that
         * would occur if using an enhanced for loop.
         */
        Collection<BlockedPeripheral<P>> blocks = blocked.values();
        while (!blocks.isEmpty()) {
            BlockedPeripheral<P> block = blocks.iterator().next();
            this.unblockPeripheral(block.peripheral);
            blocks.remove(block);
        }

        this.targetCallback = null;
        this.dropCallback = null;
        this.blockCallback = null;
        this.unblockCallback = null;

        super.close();
    }

}
