package com.whirvis.kibasan;

import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The purpose of a device seeker is to scan for input devices currently
 * connected to the system. When such device is detected, the appropriate
 * {@code InputDevice} instance and adapter will be created. Devices must
 * be polled manually after creation using {@link InputDevice#poll()}. They
 * can be retrieved from {@link #discoveredDevices}.
 * <p/>
 * Implementations should call {@link #discoverDevice(InputDevice)} when a
 * device is discovered and {@link #forgetDevice(InputDevice)} when a device
 * is forgotten.
 * <p/>
 * <b>Note:</b> For a device seeker to work as expected, it must be told to
 * perform device scans periodically via {@link #seek()}. It is recommended
 * to perform a scan once every application update.
 *
 * @param <I> the input device type.
 * @see #addListener(SeekerListener)
 * @see DeviceAdapter
 */
public abstract class DeviceSeeker<I extends InputDevice> {

    private final List<I> devices;
    private final Set<SeekerListener<? super I>> listeners;
    private boolean sendingCallback;
    public final @NotNull List<I> discoveredDevices; /* read only view */

    public DeviceSeeker() {
        this.devices = new ArrayList<>();
        this.listeners = new HashSet<>();
        this.discoveredDevices = Collections.unmodifiableList(devices);
    }

    /**
     * Adds a listener to this device seeker.
     *
     * @param listener the listener to add.
     * @throws NullPointerException  if {@code listener} is {@code null}.
     * @throws IllegalStateException if this device seeker is currently
     *                               sending a callback.
     */
    public void addListener(@NotNull SeekerListener<? super I> listener) {
        Objects.requireNonNull(listener, "listener");
        this.checkNotSendingCallback();
        synchronized (listeners) {
            listeners.add(listener);
        }
    }


    /**
     * Removes a listener from this device seeker.
     *
     * @param listener the listener to remove.
     * @throws NullPointerException  if {@code listener} is {@code null}.
     * @throws IllegalStateException if this device seeker is currently
     *                               sending a callback.
     */
    public void removeListener(@NotNull SeekerListener<? super I> listener) {
        Objects.requireNonNull(listener, "listener");
        this.checkNotSendingCallback();
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void checkNotSendingCallback() {
        if (sendingCallback) {
            throw new IllegalStateException("currently sending callback");
        }
    }

    private void sendCallback(@NotNull Consumer<SeekerListener<? super I>> event) {
        this.checkNotSendingCallback();
        this.sendingCallback = true;
        synchronized (listeners) {
            for (SeekerListener<? super I> listener : listeners) {
                event.accept(listener);
            }
        }
        this.sendingCallback = false;
    }

    @MustBeInvokedByOverriders
    protected void discoverDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device");
        if (devices.contains(device)) {
            return;
        }
        devices.add(device);
        this.sendCallback(l -> l.onDiscoverDevice(this, device));
    }

    @MustBeInvokedByOverriders
    protected void forgetDevice(@NotNull I device) {
        Objects.requireNonNull(device, "device");
        if (!devices.contains(device)) {
            return;
        }
        devices.remove(device);
        this.sendCallback(l -> l.onForgetDevice(this, device));
    }

    /**
     * Called by {@link #seek()}, this method can throw any exception without
     * needing to catch it. When an exception is thrown, {@link #seek()} will
     * wrap it into a {@link InputException} and throw it to the caller.
     *
     * @throws Exception if an error occurs.
     */
    protected abstract void seekImpl() throws Exception;

    /**
     * Performs a <i>single</i> scan for devices connected to this system.
     * For continuous scanning, this method must be called periodically once
     * every application update.
     *
     * @throws InputException if an error occurs while seeking.
     */
    public final synchronized void seek() {
        try {
            this.seekImpl();
        } catch (Throwable cause) {
            this.sendCallback(l -> l.onError(this, cause));
        }
    }

}
