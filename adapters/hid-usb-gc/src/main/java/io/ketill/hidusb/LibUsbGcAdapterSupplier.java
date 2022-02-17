package io.ketill.hidusb;

import io.ketill.AdapterSupplier;
import io.ketill.IoDeviceAdapter;
import io.ketill.MappedFeatureRegistry;
import io.ketill.gc.GcController;
import org.jetbrains.annotations.NotNull;

public class LibUsbGcAdapterSupplier implements AdapterSupplier<GcController> {

    private final GcUsbHubDevice device;
    private final int slot;

    public LibUsbGcAdapterSupplier(GcUsbHubDevice device, int slot) {
        this.device = device;
        this.slot = slot;
    }

    public boolean shouldGet() {
        return device.isSlotConnected(slot);
    }

    @Override
    public @NotNull IoDeviceAdapter<GcController> get(@NotNull GcController controller, @NotNull MappedFeatureRegistry registry) {
        return new LibUsbGcAdapter(controller, registry, device, slot);
    }
}
