package com.whirvis.kibasan.adapter.gamecube;

import com.whirvis.controller.DeviceButton;
import com.whirvis.kibasan.adapter.ButtonMapping;

public class GcButtonMapping extends ButtonMapping {

	public final int gcButton;

	public GcButtonMapping(DeviceButton button, int gcButton) {
		super(button);
		this.gcButton = gcButton;
	}

}
