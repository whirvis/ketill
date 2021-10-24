package org.ardenus.engine.input.device.adapter.dualshock;

import org.ardenus.engine.input.device.adapter.AnalogMapping;
import org.ardenus.engine.input.device.feature.Lightbar;

public class Ds4LightbarMapping extends AnalogMapping<Lightbar> {
	
	public final int byteOffset;
	
	public Ds4LightbarMapping(Lightbar lightbar, int byteOffset) {
		super(lightbar);
		this.byteOffset = byteOffset;
	}
	
}
