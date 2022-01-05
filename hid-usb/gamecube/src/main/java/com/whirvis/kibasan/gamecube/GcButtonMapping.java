package com.whirvis.kibasan.gamecube;

import com.whirvis.controller.ButtonMapping;
import com.whirvis.controller.DeviceButton;

public class GcButtonMapping extends ButtonMapping {

	public final int gcButton;

	public GcButtonMapping(DeviceButton button, int gcButton) {
		super(button);
		this.gcButton = gcButton;
	}

}
