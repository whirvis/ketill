package io.ketill.hidusb;

final class DeviceInfo {

	public final int vendorId;
	public final int productId;

	DeviceInfo(int vendorId, int productId) {
		this.vendorId = vendorId;
		this.productId = productId;
	}

}
