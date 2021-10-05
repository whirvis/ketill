package org.ardenus.engine.input.device.seeker;

import org.ardenus.engine.input.device.adapter.xinput.XInputXboxControllerAdapter;
import org.ardenus.engine.input.device.controller.XboxController;

import com.github.strikerx3.jxinput.XInputDevice;
import com.github.strikerx3.jxinput.exceptions.XInputNotLoadedException;
import com.github.strikerx3.jxinput.natives.XInputConstants;

/**
 * A device seeker for {@code XboxController} devices using X-input.
 */
public class XInputDeviceSeeker extends DeviceSeeker {

	private final XboxController[] controllers;

	/**
	 * Constructs a new {@code XInputDeviceSeeker}.
	 */
	public XInputDeviceSeeker() {
		super(XboxController.class);
		this.controllers = new XboxController[XInputConstants.MAX_PLAYERS];
	}

	@Override
	protected void seek() throws XInputNotLoadedException {
		for (int i = 0; i < controllers.length; i++) {
			XboxController controller = controllers[i];
			if (controller != null) {
				if (!controller.isConnected()) {
					this.disconnect(controller);
					this.controllers[i] = null;
				}
				continue;
			}

			XInputDevice device = XInputDevice.getDeviceFor(i);
			if (device.isConnected()) {
				XInputXboxControllerAdapter adapter =
						new XInputXboxControllerAdapter(device);
				this.controllers[i] = new XboxController(adapter);
				this.connect(controllers[i]);
			}
		}
	}

}
