package com.whirvis.ketill.xinput;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import com.whirvis.ketill.IoDeviceSeeker;
import com.whirvis.ketill.xbox.XboxController;

public final class XboxSeeker extends IoDeviceSeeker<XboxController> {

    private final XboxController[] controllers;
    private final boolean xinput14;

    public XboxSeeker() {
        this.controllers = new XboxController[XInputConstants.MAX_PLAYERS];
        this.xinput14 = XInputDevice14.isAvailable();
    }

    /**
     * <b>Note:</b> Any methods which are designated by the XInput API as not
     * being thread safe <i>must</i> be wrapped in a {@code synchronized} code
     * block. <u>If this is not done, any multithreading will likely result in
     * a JVM crash.</u>
     *
     * @param playerNum the player number.
     * @return the device for the specified player.
     * @throws XInputNotLoadedException if XInput failed to load.
     */
    /* @formatter:off */
    private XInputDevice getDevice(int playerNum)
            throws XInputNotLoadedException {
        if (xinput14) {
            return XInputDevice14.getDeviceFor(playerNum);
        } else {
            return XInputDevice.getDeviceFor(playerNum);
        }
    }
    /* @formatter:on */

    @Override
    public void seekImpl() throws XInputNotLoadedException {
        for (int i = 0; i < controllers.length; i++) {
            XboxController controller = this.controllers[i];
            if (controller != null) {
                if (!controller.isConnected()) {
                    this.forgetDevice(controller);
                    this.controllers[i] = null;
                }
                continue;
            }

            XInputDevice xDevice = this.getDevice(i);

            if (!xDevice.isConnected()) {
                /*
                 * The device must always be polled, even when it is not
                 * considered connected. If this is not done, controllers
                 * connected after the program is started will not be seen
                 * by the seeker! Once discovered, the responsibilities of
                 * polling the device are handed off to the adapter.
                 */
                synchronized (xDevice) {
                    xDevice.poll();
                }
            }

            if (xDevice.isConnected()) {
                this.controllers[i] =
                        new XboxController((c, r) -> new XboxAdapter(c, r,
                                xDevice));
                this.discoverDevice(controllers[i]);
            }
        }
    }

}
