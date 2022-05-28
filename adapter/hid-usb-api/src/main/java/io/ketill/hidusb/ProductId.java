package io.ketill.hidusb;

import java.util.Objects;

/**
 * A simple descriptor for a peripheral based on its vendor ID and
 * product ID. This is used by {@link PeripheralSeeker} to identify
 * which peripherals are currently being sought after.
 */
public final class ProductId {

    private static void requireValidId(int vendorId, int productId) {
        String msgFormat = "%s 0x%X out of range";
        msgFormat += ", must be within range of 0x0000 to 0xFFFF";
        if (vendorId < 0x0000 || vendorId > 0xFFFF) {
            String msg = String.format(msgFormat, "vendorId", vendorId);
            throw new IllegalArgumentException(msg);
        } else if (productId < 0x0000 || productId > 0xFFFF) {
            String msg = String.format(msgFormat, "productId", productId);
            throw new IllegalArgumentException(msg);
        }
    }

    public final int vendorId;
    public final int productId;

    /**
     * Constructs a new {@code ProductId}.
     *
     * @param vendorId  the vendor ID.
     * @param productId the product ID.
     * @throws IllegalArgumentException if the vendor ID or product ID are
     *                                  not within range of {@code 0x0000}
     *                                  to {@code 0xFFFF}.
     */
    public ProductId(int vendorId, int productId) {
        requireValidId(vendorId, productId);
        this.vendorId = vendorId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        }
        ProductId that = (ProductId) obj;
        return this.vendorId == that.vendorId
                && this.productId == that.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vendorId, productId);
    }

    @Override
    public String toString() {
        /* @formatter:off */
        return this.getClass().getSimpleName() + "{"  +
                "vendorId="  + vendorId        + ", " +
                "productId=" + productId       + "}";
        /* @formatter:on */
    }

}
