package com.whirvis.ketill.dualshock;

import com.whirvis.ketill.hidusb.HidDeviceSeeker;
import io.ketill.psx.Ps4Controller;
import org.hid4java.HidDevice;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class HidDs4Seeker extends HidDeviceSeeker<Ps4Controller> {

    private static class Ds4Info {

        public final Ps4Controller controller;
        public final int hidId;

        public Ds4Info(Ps4Controller controller, int hidId) {
            this.controller = controller;
            this.hidId = hidId;
        }

    }

    public interface AmbiguityCallback {

        void fuck(boolean ambiguous);

    }

    /* @formatter:off */
    private static final int
            VENDOR_ID  = 0x054C,
            PRODUCT_ID = 0x05C4;
    /* @formatter:on */

    /**
     * It was opted to use the hash codes here, as they look prettier in code
     * than a really long HID string. These values were calculated using Java's
     * {@code hashCode()} method found inside the {@code String} class. The
     * original strings for these hash codes, should they be needed, are as
     * follows:
     *
     * <pre>
     * HID_USB_ID:       "vid_054c&amp;pid_05c4"
     * HID_BLUETOOTH_ID: "{00001124-0000-1000-8000-00805f9b34fb}" +
     *                   "_vid&amp;0002054c_pid&amp;05c4"
     * </pre>
     */
    /* @formatter:off */
    private static final int
            HID_USB_ID = 0x7C6790AE,
            HID_BT_ID  = 0xE32CF9F0;
    /* @formatter:on */

    private final boolean allowUsb, allowBt;
    private final Map<HidDevice, Ds4Info> ds4s;
    private boolean wasAmbiguous;
    private @Nullable AmbiguityCallback onAmbiguity;

    /**
     * If both USB and Bluetooth controllers are allowed, there is a
     * possibility that the same PlayStation 4 controller will report
     * itself as both a USB controller and Bluetooth controller.
     *
     * @param allowUsb {@code true} if USB connections should be allowed,
     *                 {@code false} otherwise.
     * @param allowBt  {@code true} if Bluetooth connections should be allowed,
     *                 {@code false} otherwise.
     * @throws IllegalArgumentException if both {@code allowUsb} and
     *                                  {@code allowBt} are {@code false}.
     */
    public HidDs4Seeker(boolean allowUsb, boolean allowBt) {
        if (!allowUsb && !allowBt) {
            throw new IllegalArgumentException("must allow USB or Bluetooth");
        }

        this.allowUsb = allowUsb;
        this.allowBt = allowBt;
        this.ds4s = new HashMap<>();

        this.seekDevice(VENDOR_ID, PRODUCT_ID);
    }

    /**
     * Constructs a new {@code HidDs4Seeker} with support for both USB and
     * Bluetooth controllers.
     * <p>
     * Since both USB and Bluetooth controllers are allowed, there exists a
     * possibility that the same PlayStation 4 controller will report itself
     * as both a USB controller and Bluetooth controller.
     */
    public HidDs4Seeker() {
        this(true, true);
    }

    public void onAmbiguityChange(@Nullable AmbiguityCallback callback) {
        this.onAmbiguity = callback;
    }

    private void checkAmbiguity() {
        boolean hasUsb = false, hasBt = false;
        for (Ds4Info info : ds4s.values()) {
            if (info.hidId == HID_USB_ID) {
                hasUsb = true;
            } else if (info.hidId == HID_BT_ID) {
                hasBt = true;
            }
        }

        /*
         * When both USB and Bluetooth controllers are allowed, there exists
         * a possibility that the same PlayStation 4 controller will report
         * itself as both a USB controller and Bluetooth controller. There's
         * no way to tell which USB and Bluetooth controller are the same
         * physical device. As such, the best course of action is to send an
         * event, notifying listeners of the ambiguity.
         */
        boolean ambiguous = hasUsb && hasBt;
        if (!wasAmbiguous && ambiguous) {
            if (onAmbiguity != null) {
                onAmbiguity.fuck(true);
            }
            this.wasAmbiguous = true;
        } else if (wasAmbiguous && !ambiguous) {
            if (onAmbiguity != null) {
                onAmbiguity.fuck(false);
            }
            this.wasAmbiguous = false;
        }
    }

    @Override
    protected void onConnect(HidDevice device) {
        String[] path = device.getPath().split("#");
        int hidId = path[1].hashCode();

        Ds4HidAdapter adapter = null;
        if (hidId == HID_USB_ID && allowUsb) {
            adapter = new Ds4UsbAdapter(device);
        } else if (hidId == HID_BT_ID && allowBt) {
            adapter = new Ds4BtAdapter(device);
        }

        if (adapter != null) {
            Ps4Controller controller = new Ps4Controller(adapter);
            ds4s.put(device, new Ds4Info(controller, hidId));
            this.discoverDevice(controller);
            this.checkAmbiguity();
        }
    }

    @Override
    protected void onDisconnect(HidDevice device) {
        Ds4Info info = ds4s.remove(device);
        if (info != null) {
            this.forgetDevice(info.controller);
            this.checkAmbiguity();
        }
    }

    @Override
    protected void onTrouble(HidDevice device, Throwable cause) {
        /* TODO: handle this situation */
    }

    @Override
    protected void poll(HidDevice device) {
        /* nothing to poll */
    }

}
