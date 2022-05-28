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
 * virtual or physical, attached to the system. The process for connecting
 * peripherals is as follows:
 * <ul>
 * <li>1. A peripheral is attached to the system.</li>
 * <li>2. The method {@link #setupPeripheral(Object)} is called. By default,
 * this does nothing. However, it can be overridden to perform additional
 * device setup. If an exception is thrown,
 * {@link #blockPeripheral(Object, boolean)} will be called for the
 * peripheral, with {@code unblockAfterDetach} being {@code true}.</li>
 * <li>3. The method {@link #peripheralConnected(Object)} is called. Here,
 * the peripheral can be discovered as one or more devices by instantiating
 * an I/O device and then calling {@link #discoverDevice(IoDevice)}.</li>
 * </ul>
 * <p>
 * <b>Note:</b> Before calling {@link #seek()}, the peripheral seeker
 * must be told what to seek via {@link #targetProduct(ProductId)}. An
 * {@code IllegalStateException} will be thrown is this is neglected.
 * Furthermore, scans must also be performed periodically for this to
 * work as expected. It is recommended to perform a scan once every
 * application update.
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
     * Used by the peripheral seeker to determine if two instances of a
     * peripheral instance are the same. If the peripheral type provides
     * a proper implementation of {@link Object#hashCode()}, feel free
     * to implement this method as:
     * <pre>
     * &#64;Override
     * protected int getHash(@NotNull P peripheral) {
     *     return peripheral.hashCode();
     * }
     * </pre>
     * <b>Otherwise, generate one using unique data fields from the
     * peripheral.</b> The {@link Objects#hash(Object...)} utility
     * method can be used to achieve this easily.
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
     * Indicates to the seeker it should connect peripherals with the
     * specified product ID.
     *
     * @param id the ID of the product to target.
     * @throws NullPointerException  if {@code id} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #peripheralConnected(Object)
     */
    protected synchronized void targetProduct(@NotNull ProductId id) {
        Objects.requireNonNull(id, "id cannot be null");
        this.requireOpen();
        if (this.isTargetingProduct(id)) {
            return; /* already targeted */
        }

        targeted.add(id);
        this.productTargeted(id);
        if (targetCallback != null) {
            targetCallback.accept(this, id);
        }
    }

    /**
     * Indicates to the seeker it should connect peripheral with the
     * specified product ID.
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
     * Indicates to the seeker it should no longer connect peripherals
     * with the specified product ID. Peripherals with a matching ID
     * that are currently connected will be disconnected.
     *
     * @param id the ID of the product to drop.
     * @throws NullPointerException  if {@code id} is {@code null}.
     * @throws IllegalStateException if this peripheral seeker has been
     *                               closed via {@link #close()}.
     * @see #peripheralDisconnected(Object)
     */
    protected synchronized void dropProduct(@NotNull ProductId id) {
        Objects.requireNonNull(id, "id cannot be null");
        this.requireOpen();
        if (!this.isTargetingProduct(id)) {
            return;
        }

        targeted.remove(id);

        /*
         * Disconnect all connected peripherals with a matching product ID.
         * This must be done before detaching them to preserve the natural
         * order of peripheral operations.
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
                this.detachPeripheral(peripheral);
            }
        }

        /* execute callback after consequences */
        this.productDropped(id);
        if (dropCallback != null) {
            dropCallback.accept(this, id);
        }
    }

    /**
     * Indicates to the seeker it should no longer connect peripherals
     * with the specified product ID. Peripherals with a matching ID
     * that are currently connected will be disconnected.
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
     * When blocked, a peripheral is disconnected (assuming it was connected
     * when calling this method). Afterwards, it will not be able to reconnect
     * after detaching (unless {@code unblockAfterDetach} is {@code true}).
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
    protected synchronized void blockPeripheral(@NotNull P peripheral,
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

        this.disconnectPeripheral(peripheral, true);

        /* execute callback after consequences */
        this.peripheralBlocked(block);
        if (blockCallback != null) {
            blockCallback.accept(this, block);
        }
    }

    /**
     * When blocked, a peripheral is disconnected (assuming it was connected
     * when calling this method). Afterwards, it will not be able to reconnect
     * after detaching (unless {@code unblockAfterDetach} is {@code true}).
     * <p>
     * This method is a shorthand for
     * {@link #blockPeripheral(Object, Throwable, boolean)},
     * with the argument for {@code cause} being {@code null}.
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
    protected synchronized void unblockPeripheral(@NotNull P peripheral) {
        Objects.requireNonNull(peripheral, "peripheral cannot be null");
        this.requireOpen();

        int peripheralHash = this.getHash(peripheral);
        BlockedPeripheral<P> block = blocked.remove(peripheralHash);
        if (block == null) {
            return; /* peripheral not blocked */
        }

        this.peripheralUnblocked(block);
        if (unblockCallback != null) {
            unblockCallback.accept(this, block);
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
     * Called periodically by {@link #seek()} per the scan interval
     * specified during construction. This method should return <i>all</i>
     * peripherals currently connected to the system. Peripherals will be
     * filtered out before being attached using their product ID.
     *
     * @return all peripherals currently connected to the system.
     * @see #isTargetingProduct(ProductId)
     */
    protected abstract @NotNull Collection<@NotNull P> scanPeripherals();

    private void attachPeripheral(P peripheral) {
        if (!this.isTargetingPeripheral(peripheral)) {
            return;
        }

        /*
         * When a peripheral is blocked, it is disconnected, but not detached
         * (it is only detached when disconnect from the system). As such, we
         * only make sure we don't add the peripheral to the list again. The
         * connectPeripheral() method will always be called. It will ensure
         * a peripheral is not connected twice in error.
         */
        if (!attached.contains(peripheral)) {
            attached.add(peripheral);
        }

        this.connectPeripheral(peripheral);
    }

    /*
     * Like disconnectPeripheral(), there used to be a parameter called
     * requireAttached. However, this was false in every usage. As such,
     * the parameter was removed from this method.
     */
    private void detachPeripheral(P peripheral) {
        int peripheralHash = this.getHash(peripheral);
        BlockedPeripheral<P> listing = blocked.get(peripheralHash);
        if (listing != null && listing.unblockAfterDetach) {
            this.unblockPeripheral(peripheral);
        }

        attached.remove(peripheral);
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

    private void connectPeripheral(@NotNull P peripheral) {
        if (connected.contains(peripheral)) {
            return;
        }

        if (this.isPeripheralBlocked(peripheral)) {
            return;
        }

        boolean setupSuccessful;
        try {
            this.setupPeripheral(peripheral);
            setupSuccessful = true;
        } catch (Throwable cause) {
            this.peripheralSetupFailed(peripheral, cause);
            setupSuccessful = false;
        }

        /*
         * Its possible the peripheral somehow got blocked while setting up.
         * As such, check if the peripheral is blocked again. If that turns
         * out to be the case, do not finish connection.
         */
        if (this.isPeripheralBlocked(peripheral)) {
            return;
        }

        /*
         * Only connect the peripheral if setup was successful. If it fails
         * for any reason, proper communication will likely not be possible.
         * Furthermore, the user can listen for setup failure by overriding
         * the peripheralSetupFailed() method.
         */
        if (setupSuccessful) {
            connected.add(peripheral);
            this.peripheralConnected(peripheral);
        }
    }

    /**
     * Called just before a peripheral is connected. By default, this
     * method does nothing. This can be used to perform setup for the
     * peripheral (which is often error-prone) before final connection.
     * This method can throw any exception without needing to catch it.
     * <p>
     * <b>Note:</b> If this method does throw an exception, connection
     * of the peripheral will be aborted. Furthermore, it will be blocked
     * until it is later detached.
     *
     * @param peripheral the peripheral.
     * @throws Exception if an error occurs.
     * @see #peripheralSetupFailed(Object, Throwable)
     * @see #peripheralConnected(Object)
     */
    @SuppressWarnings("RedundantThrows")
    protected void setupPeripheral(@NotNull P peripheral) throws Exception {
        /* optional implement */
    }

    /**
     * Called when {@link #setupPeripheral(Object)} fails as a result of
     * an exception. <b>By default, this blocks the peripheral.</b> This
     * can be used to perform measures necessary to perform corrective
     * measures (e.g., freeing resources).
     *
     * @param peripheral the peripheral.
     * @param cause      the cause for setup failure.
     */
    protected void peripheralSetupFailed(@NotNull P peripheral,
                                         @NotNull Throwable cause) {
        this.blockPeripheral(peripheral, cause, true);
    }

    /**
     * Called when a targeted peripheral has connected. This is executed
     * after setup is performed. If setup fails (due to an exception),
     * this method will <i>not</i> be called.
     * <p>
     * <b>Note:</b> Connected peripherals are <i>not</i> discovered.
     * They must be discovered using {@link #discoverDevice(IoDevice)}.
     * Furthermore, it is by intention that no user callback exists for
     * this event. It is not for the user to know when peripherals are
     * connected! They should only be listening for I/O device discovery.
     *
     * @param peripheral the peripheral.
     * @see #setupPeripheral(Object)
     */
    protected abstract void peripheralConnected(@NotNull P peripheral);

    private void disconnectPeripheral(@NotNull P peripheral,
                                      boolean requireConnected) {
        if (!connected.contains(peripheral) && requireConnected) {
            return; /* not connected when required to be */
        }

        /*
         * The peripheral must be removed from the connected list before
         * performing shutdown. It is very possible the shutdown code will
         * call blockPeripheral() (which calls this method). Removing it
         * from the list before this can happen prevents a stack overflow.
         *
         * Furthermore, it doesn't make sense for a device that is being
         * shutdown to be considered still connected (as detaching the
         * device is what triggers the shutdown code).
         */
        connected.remove(peripheral);

        try {
            this.shutdownPeripheral(peripheral);
        } catch (Throwable cause) {
            /*
             * Only notify the peripheral seeker an error has occurred when
             * detaching a peripheral. Do not block it! Blocking it would
             * resolve nothing, as communication has already ended. If the
             * user desires, they can block it themselves.
             */
            this.peripheralShutdownFailed(peripheral, cause);
        }

        this.peripheralDisconnected(peripheral);
    }

    /**
     * Called just before a peripheral is disconnected. By default, this
     * method does nothing. This can be used to perform shutdown for the
     * peripheral (which is often error-prone) before final disconnection.
     * This method can throw any exception without needing to catch it.
     * <p>
     * <b>Note:</b> If this method does throw an exception, disconnection
     * of the peripheral will <i>not</i> be aborted. Furthermore, it will
     * not be blocked (as that would be pointless at this stage).
     *
     * @param peripheral the peripheral.
     * @throws Exception if an error occurs.
     * @see #peripheralSetupFailed(Object, Throwable)
     * @see #peripheralConnected(Object)
     */
    @SuppressWarnings("RedundantThrows")
    protected void shutdownPeripheral(@NotNull P peripheral) throws Exception {
        /* optional implement */
    }

    /**
     * Called when {@link #shutdownPeripheral(Object)} as a result of an
     * exception. <b>By default, this method does nothing.</b> This can
     * be used to perform corrective measures (e.g., freeing resources).
     *
     * @param peripheral the peripheral.
     * @param cause      the cause for setup failure.
     */
    protected void peripheralShutdownFailed(@NotNull P peripheral,
                                            @NotNull Throwable cause) {
        /* optional implement */
    }

    /**
     * Called when a previously connected peripheral has disconnected.
     * This is executed after shutdown is performed, regardless if
     * shutdown was successfully executed.
     * <p>
     * <b>Note:</b> Disconnected peripherals are <i>not</i> forgotten.
     * They must be forgotten using {@link #forgetDevice(IoDevice)}.
     * Furthermore, it is by intention that no user callback exists for
     * this event. It is not for the user to know when peripherals are
     * disconnected! They should only be listening for I/O device discovery.
     *
     * @param peripheral the peripheral.
     * @see #shutdownPeripheral(Object)
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
                    this.detachPeripheral(peripheral);
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
