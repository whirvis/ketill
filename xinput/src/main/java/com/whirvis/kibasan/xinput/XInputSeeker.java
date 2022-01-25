package com.whirvis.kibasan.xinput;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import com.whirvis.kibasan.DeviceSeeker;
import com.whirvis.kibasan.xbox.XboxController;

public final class XInputSeeker extends DeviceSeeker<XboxController> {

    private final XboxController[] controllers;
    private final boolean xinput14;

    public XInputSeeker() {
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
                XInputAdapter adapter = new XInputAdapter(device);
                this.controllers[i] = new XboxController(adapter);
                this.discoverDevice(controllers[i]);
            }
        }
    }

}
