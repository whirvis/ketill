package com.whirvis.kibasan.dualshock;

import com.whirvis.controller.AnalogMapping;
import com.whirvis.kibasan.psx.Lightbar;

public class Ds4LightbarMapping extends AnalogMapping<Lightbar> {
	
	public final int byteOffset;
	
	public Ds4LightbarMapping(Lightbar lightbar, int byteOffset) {
		super(lightbar);
		this.byteOffset = byteOffset;
	}
	
}
