package com.whirvis.kibasan.seeker;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.XInputDevice14;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;
import com.whirvis.kibasan.DeviceSeeker;
import com.whirvis.kibasan.adapter.xinput.XboxAdapter;
import com.whirvis.kibasan.xbox.XboxController;

public class XInputSeeker extends DeviceSeeker {

	private final XboxController[] controllers;
	private boolean xinput14;

	public XInputSeeker() {
		super(XboxController.class);
		this.controllers = new XboxController[XInputConstants.MAX_PLAYERS];
		this.xinput14 = XInputDevice14.isAvailable();
	}

	private XInputDevice getDevice(int playerNum)
			throws XInputNotLoadedException {
		if (xinput14) {
			log.info("Using XInput 1.4");
			return XInputDevice14.getDeviceFor(playerNum);
		} else {
			return XInputDevice.getDeviceFor(playerNum);
		}
	}

	@Override
	protected void seek() throws XInputNotLoadedException {
		for (int i = 0; i < controllers.length; i++) {
			XboxController controller = controllers[i];
			if (controller != null) {
				if (!controller.isConnected()) {
					this.unregister(controller);
					this.controllers[i] = null;
				}
				continue;
			}

			XInputDevice device = this.getDevice(i);
			if (device.isConnected()) {
				XboxAdapter adapter = new XboxAdapter(device);
				this.controllers[i] = new XboxController(adapter);
				this.register(controllers[i]);
			}
		}
	}

}
