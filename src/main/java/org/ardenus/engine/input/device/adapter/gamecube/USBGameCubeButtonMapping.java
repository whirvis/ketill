package org.ardenus.engine.input.device.adapter.gamecube;

import org.ardenus.engine.input.device.adapter.ButtonMapping;
import org.ardenus.engine.input.device.feature.DeviceButton;

public class USBGameCubeButtonMapping extends ButtonMapping {

	public final int gcButton;

	public USBGameCubeButtonMapping(DeviceButton button, int gcButton) {
		super(button);
		this.gcButton = gcButton;
	}

}
