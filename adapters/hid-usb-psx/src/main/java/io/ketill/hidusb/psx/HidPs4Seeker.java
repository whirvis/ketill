package io.ketill.hidusb.psx;

import io.ketill.AdapterSupplier;
import io.ketill.hidusb.HidDeviceSeeker;
import io.ketill.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class HidPs4Seeker extends HidDeviceSeeker<Ps4Controller> {

    private static final int VENDOR_SONY = 0x054C;
    private static final int PRODUCT_DS4 = 0x05C4;
    private static final int PRODUCT_DS4_REV = 0x09CC;

    private final boolean allowUsb, allowBt;
    private final Map<HidDevice, HidPs4Session> sessions;
    private boolean ambiguous;

    private @Nullable HidPs4AmbiguityCallback ambiguityCallback;

    /**
     * If both USB and Bluetooth controllers are allowed, there exists a
     * possibility a single PS4 controller will report itself as both a
     * USB controller and Bluetooth controller.
     *
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @param allowUsb       {@code true} if USB connections should be allowed,
     *                       {@code false} otherwise.
     * @param allowBt        {@code true} if Bluetooth connections should be
     *                       allowed, {@code false} otherwise.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL};
     *                                  if {@code scanIntervalMs} is greater
     *                                  than {@value Integer#MAX_VALUE};
     *                                  if both{@code allowUsb} and
     *                                  {@code allowBt} are {@code false}.
     * @see #isAmbiguous()
     * @see #onAmbiguity(HidPs4AmbiguityCallback)
     */
    public HidPs4Seeker(long scanIntervalMs, boolean allowUsb,
                        boolean allowBt) {
        super(scanIntervalMs);
        if (!allowUsb && !allowBt) {
            throw new IllegalArgumentException("must allow USB or Bluetooth");
        }

        this.allowUsb = allowUsb;
        this.allowBt = allowBt;
        this.sessions = new HashMap<>();

        this.targetProduct(VENDOR_SONY, PRODUCT_DS4);
        this.targetProduct(VENDOR_SONY, PRODUCT_DS4_REV);
    }

    /**
     * Constructs a new {@code HidDs4Seeker} with the argument for
     * {@code scanIntervalMs} being {@value #MINIMUM_SCAN_INTERVAL}.
     * <p>
     * If both USB and Bluetooth controllers are allowed, there exists a
     * possibility a single PS4 controller will report itself as both a
     * USB controller and Bluetooth controller.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a device scan from being performed unless
     * enough time has elapsed between method calls.
     *
     * @param allowUsb {@code true} if USB connections should be allowed,
     *                 {@code false} otherwise.
     * @param allowBt  {@code true} if Bluetooth connections should be
     *                 allowed, {@code false} otherwise.
     * @throws IllegalArgumentException if both {@code allowUsb} and
     *                                  {@code allowBt} are {@code false}.
     * @see #isAmbiguous()
     * @see #onAmbiguity(HidPs4AmbiguityCallback)
     */
    public HidPs4Seeker(boolean allowUsb, boolean allowBt) {
        this(MINIMUM_SCAN_INTERVAL, allowUsb, allowBt);
    }

    /**
     * Constructs a new {@code HidDs4Seeker} with support for both USB and
     * Bluetooth controllers.
     * <p>
     * Since both USB and Bluetooth controllers are allowed, there exists
     * a possibility a single PS4 controller will report itself as both a
     * USB controller and Bluetooth controller.
     * <p>
     * <b>Note:</b> The scan interval does <i>not</i> cause {@link #seek()}
     * to block. It only prevents a device scan from being performed unless
     * enough time has elapsed between method calls.
     *
     * @param scanIntervalMs the interval in milliseconds between device
     *                       enumeration scans. This does <i>not</i> cause
     *                       {@link #seek()} to block. It only prevents a
     *                       device scan from being performed unless enough
     *                       time has elapsed between method calls.
     * @throws IllegalArgumentException if {@code scanIntervalMs} is less
     *                                  than {@value #MINIMUM_SCAN_INTERVAL};
     *                                  if {@code scanIntervalMs} is greater
     *                                  than {@value Integer#MAX_VALUE}.
     * @see #isAmbiguous()
     * @see #onAmbiguity(HidPs4AmbiguityCallback)
     */
    public HidPs4Seeker(long scanIntervalMs) {
        this(scanIntervalMs, true, true);
    }

    /**
     * Constructs a new {@code HidDs4Seeker} with support for both USB and
     * Bluetooth controllers. The scan interval for this device seeker is
     * the default ({@value #MINIMUM_SCAN_INTERVAL}).
     * <p>
     * Since both USB and Bluetooth controllers are allowed, there exists
     * a possibility a single PS4 controller will report itself as both a
     * USB controller and Bluetooth controller.
     *
     * @see #isAmbiguous()
     * @see #onAmbiguity(HidPs4AmbiguityCallback)
     */
    public HidPs4Seeker() {
        this(MINIMUM_SCAN_INTERVAL);
    }

    /**
     * When both USB and Bluetooth controllers are allowed, there exists a
     * possibility a single PS4 controller will report itself as a USB and
     * Bluetooth controller <i>at the same time.</i> There is no known way
     * to determine if a USB and Bluetooth PS4 controller are the same
     * physical device.
     *
     * @return {@code true} if there is currently ambiguity between PS4
     * controllers, {@code false} otherwise.
     * @see #onAmbiguity(HidPs4AmbiguityCallback)
     */
    public boolean isAmbiguous() {
        return this.ambiguous;
    }

    /**
     * Sets the callback for when ambiguity between PS4 controllers is
     * detected or resolved. If this callback was set <i>after</i> the
     * ambiguity was detected, it will not be called. The status of
     * ambiguity will have to be fetched via {@link #isAmbiguous()}.
     *
     * @param callback the code execute when ambiguity is detected. A value
     *                 of {@code null} is permitted, and will result in
     *                 nothing being executed.
     */
    public void onAmbiguity(@Nullable HidPs4AmbiguityCallback callback) {
        this.ambiguityCallback = callback;
    }

    private void checkAmbiguity() {
        boolean hasUsb = false, hasBt = false;
        for (HidPs4Session info : sessions.values()) {
            switch (info.type) {
                case USB:
                    hasUsb = true;
                    break;
                case BT:
                    hasBt = true;
                    break;
            }
        }

        boolean nowAmbiguous = hasUsb && hasBt;
        if (!this.ambiguous && nowAmbiguous) {
            if (ambiguityCallback != null) {
                ambiguityCallback.execute(true);
            }
            this.ambiguous = true;
        } else if (this.ambiguous && !nowAmbiguous) {
            if (ambiguityCallback != null) {
                ambiguityCallback.execute(false);
            }
            this.ambiguous = false;
        }
    }

    @Override
    protected void peripheralConnected(@NotNull HidDevice device) {
        HidPs4Type type = HidPs4Type.fromPath(device.getPath());

        AdapterSupplier<Ps4Controller> adapterSupplier = null;
        if (type == HidPs4Type.USB && allowUsb) {
            adapterSupplier = (c, r) -> new HidPs4AdapterUsb(c, r, device);
        } else if (type == HidPs4Type.BT && allowBt) {
            adapterSupplier = (c, r) -> new HidPs4AdapterBt(c, r, device);
        }

        if (adapterSupplier != null) {
            Ps4Controller controller = new Ps4Controller(adapterSupplier);
            sessions.put(device, new HidPs4Session(controller, type));
            this.discoverDevice(controller);
            this.checkAmbiguity();
        }
    }

    @Override
    protected void peripheralDisconnected(@NotNull HidDevice device) {
        HidPs4Session session = sessions.remove(device);
        if (session != null) {
            this.forgetDevice(session.controller);
            this.checkAmbiguity();
        }
    }

}
