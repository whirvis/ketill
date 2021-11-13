package org.ardenus.input.adapter.dualshock;

import org.ardenus.input.adapter.AnalogMapping;
import org.ardenus.input.feature.Lightbar;

public class Ds4LightbarMapping extends AnalogMapping<Lightbar> {
	
	public final int byteOffset;
	
	public Ds4LightbarMapping(Lightbar lightbar, int byteOffset) {
		super(lightbar);
		this.byteOffset = byteOffset;
	}
	
}
