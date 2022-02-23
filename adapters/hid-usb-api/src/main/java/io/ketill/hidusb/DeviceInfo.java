package io.ketill.hidusb;

final class DeviceInfo {

    static void requireValidId(int vendorId, int productId) {
        if (vendorId < 0x0000 || vendorId > 0xFFFF) {
            String msg = String.format("vendorId 0x%X out of range, must "
                    + "be within 0x0000 to 0xFFFF", vendorId);
            throw new IllegalArgumentException(msg);
        } else if (productId < 0x0000 || productId > 0xFFFF) {
            String msg = String.format("productId 0x%X out of range, must "
                    + "be within 0x0000 to 0xFFFF", productId);
            throw new IllegalArgumentException(msg);
        }
    }

    final int vendorId;
    final int productId;

    DeviceInfo(int vendorId, int productId) {
        requireValidId(vendorId, productId);
        this.vendorId = vendorId;
        this.productId = productId;
    }

}
