package com.whirvis.kibasan.hidusb;

public class DeviceDesc {

	public static String getStr(int vendorId, int productId) {
		String vendorStr = String.format("%04X", vendorId);
		String productStr = String.format("%04X", productId);
		return vendorStr + ":" + productStr;
	}

	public final int vendorId;
	public final int productId;

	public DeviceDesc(int vendorId, int productId) {
		this.vendorId = vendorId;
		this.productId = productId;
	}

}
