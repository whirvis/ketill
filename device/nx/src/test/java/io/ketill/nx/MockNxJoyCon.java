package io.ketill.nx;

import io.ketill.AdapterSupplier;
import org.jetbrains.annotations.NotNull;

class MockNxJoyCon extends NxJoyCon {

    MockNxJoyCon(@NotNull AdapterSupplier<?> adapterSupplier) {
        super("nx_joycon_mock", adapterSupplier, null, null, null, null);
    }

}
