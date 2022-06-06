package io.ketill.xinput;

import com.github.strikerx3.jxinput.XInputAxes;
import com.github.strikerx3.jxinput.XInputButtons;
import com.github.strikerx3.jxinput.XInputComponents;
import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.enums.XInputBatteryDeviceType;
import com.github.strikerx3.jxinput.enums.XInputBatteryLevel;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe wrapper for {@link XInputDevice}.
 * <p>
 * <b>Visibility:</b> This class is {@code package-private} since only
 * the functionality required by {@link XInputXboxAdapter} to do its job
 * is provided. As such, it is hidden from users.
 * <p>
 * <b>Thread safety:</b> This class is <i>thread-safe.</i> A lock is
 * utilized to ensure multiple threads cannot perform operations like
 * polling the underlying device at the same time.
 *
 * @see #poll()
 */
final class AtomicXInputDevice {

    private static final XInputBatteryDeviceType GAMEPAD =
            XInputBatteryDeviceType.GAMEPAD;

    private final XInputDevice device;
    private final Lock deviceReadLock;
    private final Lock deviceWriteLock;

    private final AtomicBoolean connected;
    private XInputButtons buttons;
    private XInputAxes axes;
    private XInputBatteryLevel batteryLevel;

    AtomicXInputDevice(@NotNull XInputDevice device) {
        this.device = device;

        ReadWriteLock deviceLock = new ReentrantReadWriteLock();
        this.deviceReadLock = deviceLock.readLock();
        this.deviceWriteLock = deviceLock.writeLock();

        this.connected = new AtomicBoolean();
    }

    boolean supportsGuideButton() {
        return XInputDevice.isGuideButtonSupported();
    }

    boolean supportsBatteryLevel() {
        return device instanceof XInputDevice14;
    }

    boolean isConnected() {
        deviceReadLock.lock();
        try {
            return connected.get();
        } finally {
            deviceReadLock.unlock();
        }
    }

    boolean isPressed(@NotNull XInputButtonAccessor accessor) {
        deviceReadLock.lock();
        try {
            if (buttons != null) {
                return accessor.isPressed(buttons);
            } else {
                return false;
            }
        } finally {
            deviceReadLock.unlock();
        }
    }

    float getAxis(@NotNull XInputAxisAccessor accessor) {
        deviceReadLock.lock();
        try {
            if (axes != null) {
                return accessor.get(axes);
            } else {
                return 0.0F;
            }
        } finally {
            deviceReadLock.unlock();
        }
    }

    float getBatteryLevel() {
        deviceReadLock.lock();
        try {
            if (batteryLevel == null) {
                return XInput.BATTERY_LEVEL_UNKNOWN;
            }

            switch (batteryLevel) {
                case EMPTY:
                    return XInput.BATTERY_LEVEL_EMPTY;
                case LOW:
                    return XInput.BATTERY_LEVEL_LOW;
                case MEDIUM:
                    return XInput.BATTERY_LEVEL_MEDIUM;
                case FULL:
                    return XInput.BATTERY_LEVEL_FULL;
                default:
                    /* this should never happen */
                    String msg = "unexpected battery level " + batteryLevel;
                    throw new XInputException(msg);
            }
        } finally {
            deviceReadLock.unlock();
        }
    }

    void setVibration(int rumbleCoarse, int rumbleFine) {
        deviceWriteLock.lock();
        try {
            device.setVibration(rumbleCoarse, rumbleFine);
        } finally {
            deviceWriteLock.unlock();
        }
    }

    void poll() {
        deviceWriteLock.lock();
        try {
            device.poll();
            if (device.isConnected()) {
                connected.set(true);

                XInputComponents comps = device.getComponents();
                this.axes = comps.getAxes();
                this.buttons = comps.getButtons();

                /* @formatter:off */
                if (device instanceof XInputDevice14) {
                    this.batteryLevel = ((XInputDevice14) device)
                            .getBatteryInformation(GAMEPAD)
                            .getLevel();
                }
                /* @formatter:on */
            } else {
                connected.set(false);

                this.axes = null;
                this.buttons = null;
                this.batteryLevel = null;
            }
        } finally {
            deviceWriteLock.unlock();
        }
    }

}
