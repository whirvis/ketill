package io.ketill.hidusb;

public class DeviceInfo {

	public final int vendorId;
	public final int productId;

	protected DeviceInfo(int vendorId, int productId) {
		this.vendorId = vendorId;
		this.productId = productId;
	}

}
