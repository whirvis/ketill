package io.ketill.psx;

import io.ketill.AdapterSupplier;
import org.jetbrains.annotations.NotNull;

class MockPsxController extends PsxController {

    MockPsxController(@NotNull AdapterSupplier<?> adapterSupplier) {
        super("psx", adapterSupplier, null, null);
    }

}
