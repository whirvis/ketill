package org.ardenus.input.adapter.gamecube;

import org.ardenus.input.adapter.ButtonMapping;
import org.ardenus.input.feature.DeviceButton;

public class GcButtonMapping extends ButtonMapping {

	public final int gcButton;

	public GcButtonMapping(DeviceButton button, int gcButton) {
		super(button);
		this.gcButton = gcButton;
	}

}
