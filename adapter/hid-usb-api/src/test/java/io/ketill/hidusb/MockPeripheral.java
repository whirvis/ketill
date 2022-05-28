package io.ketill.hidusb;

class MockPeripheral {

    ProductId id;
    int hashCode;
    private final boolean useSuperHashCode;

    MockPeripheral(ProductId id, int hashCode) {
        this.id = id;
        this.hashCode = hashCode;
        this.useSuperHashCode = false;
    }

    MockPeripheral(int hashCode) {
        this(null, hashCode);
    }

    MockPeripheral() {
        this.id = null;
        this.hashCode = 0;
        this.useSuperHashCode = true;
    }

    @Override
    public int hashCode() {
        if (useSuperHashCode) {
            return super.hashCode();
        }
        return this.hashCode;
    }

}
