package com.whirvis.kibasan.xinput;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import com.whirvis.kibasan.DeviceSeeker;
import com.whirvis.kibasan.xbox.XboxController;

public final class XboxSeeker extends DeviceSeeker<XboxController> {

    private final XboxController[] controllers;
    private final boolean xinput14;

    public XboxSeeker() {
        this.controllers = new XboxController[XInputConstants.MAX_PLAYERS];
        this.xinput14 = XInputDevice14.isAvailable();
    }

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

            XInputDevice device = this.getDevice(i);
            if (device.isConnected()) {
                this.controllers[i] =
                        new XboxController((c, r) -> new XboxAdapter(c, r,
                                device));
                this.discoverDevice(controllers[i]);
            } else {
                /*
                 * The device must always be polled, even when it is not
                 * considered connecting. If this is not done, controllers
                 * connected after the program is started will not be seen
                 * by the seeker! However, once it has been discovered, the
                 * responsibilities of polling the device are handed off to
                 * the controller's respective adapter.
                 */
                device.poll();
            }
        }
    }

}
