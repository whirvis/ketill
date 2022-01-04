package com.whirvis.kibasan.adapter.gamecube;

import com.whirvis.kibasan.adapter.ButtonMapping;
import com.whirvis.kibasan.feature.DeviceButton;

public class GcButtonMapping extends ButtonMapping {

	public final int gcButton;

	public GcButtonMapping(DeviceButton button, int gcButton) {
		super(button);
		this.gcButton = gcButton;
	}

}
