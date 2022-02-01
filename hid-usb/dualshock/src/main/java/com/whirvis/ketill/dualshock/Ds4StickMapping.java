package com.whirvis.ketill.dualshock;

public class Ds4StickMapping {

	public final int byteOffsetX;
	public final int byteOffsetY;
	public final int thumbByteOffset;
	public final int thumbBitOffset;
	
	public Ds4StickMapping(int byteOffsetX, int byteOffsetY,
						   int thumbByteOffset, int thumbBitOffset) {
		this.byteOffsetX = byteOffsetX;
		this.byteOffsetY = byteOffsetY;
		this.thumbByteOffset = thumbByteOffset;
		this.thumbBitOffset = thumbBitOffset;
	}

}
