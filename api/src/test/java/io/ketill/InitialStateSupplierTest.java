package io.ketill;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class InitialStateSupplierTest {

    @Test
    void testWrap() {
        /*
         * When a null supplier is given to wrap, this method should return
         * a null value. Otherwise, the inevitable NullPointerException will
         * only be delayed.
         */
        assertNull(InitialStateSupplier.wrap(null));

        Object supplied = new Object();
        Supplier<Object> supplier = () -> supplied;
        InitialStateSupplier<Object> wrapped =
                InitialStateSupplier.wrap(supplier);

        /*
         * Since this method wraps the original supplier, it should return
         * the same exact value.
         */
        IoFeature<?, ?> feature = mock(IoFeature.class);
        IoDeviceObserver device = mock(IoDeviceObserver.class);
        assertSame(supplier.get(), wrapped.get(feature, device));
    }

}
