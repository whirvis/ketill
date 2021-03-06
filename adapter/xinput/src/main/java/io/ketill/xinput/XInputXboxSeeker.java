package io.ketill.xinput;

import io.ketill.IoDeviceSeeker;
import io.ketill.xbox.XboxController;

/**
 * An {@link XboxController} seeker using XInput.
 * <p>
 * <b>Thread safety:</b> This seeker is <i>thread-safe.</i>
 *
 * @see XInputXboxAdapter
 * @see XInput#getPlayer(int)
 */
public final class XInputXboxSeeker extends IoDeviceSeeker<XboxController> {

    private final XboxController[] controllers;

    /**
     * Constructs a new {@code XInputXboxSeeker}.
     *
     * @throws XInputUnavailableException if the XInput library is not
     *                                    available on this machine.
     * @see XInput#isAvailable()
     */
    public XInputXboxSeeker() {
        XInput.requireAvailable();
        this.controllers = new XboxController[XInput.PLAYER_COUNT];
    }

    @Override
    protected void seekImpl() {
        for (int i = 0; i < controllers.length; i++) {
            XboxController controller = this.controllers[i];
            if (controller != null) {
                if (!controller.isConnected()) {
                    this.forgetDevice(controller);
                    this.controllers[i] = null;
                }
                continue;
            }

            XboxController player = XInput.getPlayer(i);
            if (player.isConnected()) {
                this.controllers[i] = player;
                this.discoverDevice(player);
            }
        }
    }

}
