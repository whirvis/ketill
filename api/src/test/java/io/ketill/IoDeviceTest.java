package io.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceTest {

    private MockIoDevice device;
    private MockIoDeviceAdapter adapter;

    @BeforeAll
    static void __init__() {
        /*
         * The device must be identifiable and have an adapter to poll for
         * input information. As such, null IDs, null adapter suppliers, and
         * null values given by the adapter supplier are illegal.
         */
        assertThrows(NullPointerException.class, () -> new MockIoDevice(null,
                MockIoDeviceAdapter::new));
        assertThrows(NullPointerException.class, () -> new MockIoDevice("mock"
                , null));
        assertThrows(NullPointerException.class, () -> new MockIoDevice("mock"
                , (d, r) -> null));

        /*
         * It makes no sense for the input device's ID to be blank.
         * Furthermore, any whitespace in an ID is illegal.
         */
        assertThrows(IllegalArgumentException.class, () -> new MockIoDevice(
                "", MockIoDeviceAdapter::new));
        assertThrows(IllegalArgumentException.class, () -> new MockIoDevice(
                "\t", MockIoDeviceAdapter::new));

        AtomicReference<MockIoDeviceAdapter> adapter = new AtomicReference<>();
        AdapterSupplier<MockIoDevice> adapterSupplier = (d, r) -> {
            adapter.set(new MockIoDeviceAdapter(d, r));
            return adapter.get();
        };

        /*
         * If not told to, the input device must not register fields marked
         * with the @FeaturePresent annotation, nor initialize the adapter.
         * This is to allow special extending classes (like Controller in
         * the "devices" module) to finish some extra setup.
         */
        MockIoDevice device = new MockIoDevice("mock", adapterSupplier, false
                , false);
        assertFalse(device.isRegistered(MockIoDevice.FEATURE));
        assertFalse(adapter.get().isInitialized());
    }

    @BeforeEach
    void setup() {
        AtomicReference<MockIoDeviceAdapter> adapter = new AtomicReference<>();
        AdapterSupplier<MockIoDevice> adapterSupplier = (d, r) -> {
            adapter.set(new MockIoDeviceAdapter(d, r));
            return adapter.get();
        };

        this.device = new MockIoDevice("mock", adapterSupplier);
        this.adapter = adapter.get();
    }

    @Test
    void initAdapter() {
        /*
         * The MockInputDevice constructed in setup() uses the default
         * arguments. As such, it should have initialized its adapter.
         */
        assertTrue(adapter.isInitialized());

        /*
         * Attempting to call initAdapter() again should result in an
         * IllegalStateException being thrown. Initializing the adapter
         * twice makes no sense, and would lead to bugs.
         */
        assertThrows(IllegalStateException.class, device::initAdapter);
    }

    @Test
    void registerFields() {
        /*
         * The MockInputDevice constructed in setup() uses the default
         * arguments. As such, it should have registered the FEATURE
         * field which is present in the MockInputDevice class and is
         * annotated with @FeaturePresent.
         */
        assertTrue(device.isRegistered(MockIoDevice.FEATURE));

        /*
         * Attempting to call registerFields() again should result in an
         * IllegalStateException being thrown. All fields marked with the
         * @FeaturePresent annotation have already been combed over.
         */
        assertThrows(IllegalStateException.class, device::registerFields);

        /*
         * It makes no sense for a feature field annotated with the
         * @FeaturePresent annotation to be private (as it would not
         * be accessible to the outside world.) Furthermore, if the
         * field is not assignable from DeviceFeature, then it cannot
         * be registered as a device feature.
         */
        assertThrows(KetillException.class,
                MockIoDevice.WithPrivateFeature::new);
        assertThrows(KetillException.class,
                MockIoDevice.WithUnassignableFeature::new);
    }

    @Test
    void isFeatureSupported() {
        assertThrows(NullPointerException.class,
                () -> device.isFeatureSupported(null));
        assertTrue(device.isFeatureSupported(MockIoDevice.FEATURE));
        assertFalse(device.isFeatureSupported(new MockIoFeature()));
    }

    @Test
    void getFeatures() {
        /*
         * The getFeatures() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertIterableEquals(device.getFeatures(),
                adapter.registry.getFeatures());
    }

    @Test
    void getRegistered() {
        /*
         * The getRegistered() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertEquals(device.getRegistered(MockIoDevice.FEATURE),
                adapter.registry.getRegistered(MockIoDevice.FEATURE));
    }

    @Test
    void isRegistered() {
        MockIoFeature feature = new MockIoFeature();

        /*
         * The isRegistered() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertEquals(device.isRegistered(feature),
                adapter.registry.isRegistered(feature));
        device.registerFeature(feature);
        assertEquals(device.isRegistered(feature),
                adapter.registry.isRegistered(feature));
    }

    @Test
    void registerFeature() {
        MockIoFeature feature = new MockIoFeature();
        AtomicBoolean registered = new AtomicBoolean();
        device.onRegisterFeature((f) -> registered.set(f == feature));

        device.registerFeature(feature);
        assertTrue(registered.get());
        assertTrue(device.isRegistered(feature));

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the device.
         */
        assertDoesNotThrow(() -> device.onRegisterFeature(null));

        /*
         * It makes no sense to register a null feature or a feature which
         * has already been registered. As such, assume these were mistakes
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.registerFeature(null));
        assertThrows(IllegalStateException.class,
                () -> device.registerFeature(feature));
    }

    @Test
    void unregisterFeature() {
        MockIoFeature feature = new MockIoFeature();
        AtomicBoolean unregistered = new AtomicBoolean();
        device.onUnregisterFeature((f) -> unregistered.set(f == feature));
        device.registerFeature(feature); /* something to unregister */

        device.unregisterFeature(feature);
        assertTrue(unregistered.get());
        assertFalse(device.isRegistered(feature));

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the device.
         */
        assertDoesNotThrow(() -> device.onUnregisterFeature(null));

        /*
         * It makes no sense to unregister a null feature or a feature which
         * has already been unregistered. As such, assume these were mistakes
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.unregisterFeature(null));
        assertThrows(IllegalStateException.class,
                () -> device.unregisterFeature(feature));
    }

    @Test
    void isConnected() {
        AtomicBoolean connected = new AtomicBoolean();
        device.onConnect(() -> connected.set(true));
        device.onDisconnect(() -> connected.set(false));

        /*
         * First test the callback for when the device is connected. Since
         * the device is disconnected when first instantiated, it will not
         * send a callback. Connecting the device here, then disconnecting
         * it, will have the disconnect callback fired as desired.
         */
        adapter.shouldBeConnected = true;
        device.poll();
        assertTrue(connected.get());
        assertTrue(device.isConnected());

        adapter.shouldBeConnected = false;
        device.poll();
        assertFalse(connected.get());
        assertFalse(device.isConnected());

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the device.
         */
        assertDoesNotThrow(() -> device.onConnect(null));
        assertDoesNotThrow(() -> device.onDisconnect(null));
    }

}