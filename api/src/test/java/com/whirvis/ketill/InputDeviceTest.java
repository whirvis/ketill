package com.whirvis.ketill;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class InputDeviceTest {

    MockInputDevice device;
    MockDeviceAdapter adapter;

    @BeforeAll
    static void __init__() {
        /*
         * The device must be identifiable and have an adapter to poll for
         * input information. As such, null IDs, null adapter suppliers, and
         * null values given by the adapter supplier are illegal.
         */
        assertThrows(NullPointerException.class,
                () -> new MockInputDevice(null, MockDeviceAdapter::new));
        assertThrows(NullPointerException.class,
                () -> new MockInputDevice("mock", null));
        assertThrows(NullPointerException.class,
                () -> new MockInputDevice("mock", (d, r) -> null));

        /*
         * It makes no sense for the input device's ID to be blank.
         * Furthermore, any whitespace in an ID is illegal.
         */
        assertThrows(IllegalArgumentException.class,
                () -> new MockInputDevice("", MockDeviceAdapter::new));
        assertThrows(IllegalArgumentException.class,
                () -> new MockInputDevice("\t", MockDeviceAdapter::new));

        AtomicReference<MockDeviceAdapter> adapter = new AtomicReference<>();
        AdapterSupplier<MockInputDevice> adapterSupplier = (d, r) -> {
            adapter.set(new MockDeviceAdapter(d, r));
            return adapter.get();
        };

        /*
         * If not told to, the input device must not register fields marked
         * with the @FeaturePresent annotation, nor initialize the adapter.
         * This is to allow special extending classes (like Controller in
         * the "devices" module) to finish some extra setup.
         */
        MockInputDevice device = new MockInputDevice("mock",
                adapterSupplier, false, false);
        assertFalse(device.isRegistered(MockInputDevice.FEATURE));
        assertFalse(adapter.get().isInitialized());
    }

    @BeforeEach
    void setup() {
        AtomicReference<MockDeviceAdapter> adapter = new AtomicReference<>();
        AdapterSupplier<MockInputDevice> adapterSupplier = (d, r) -> {
            adapter.set(new MockDeviceAdapter(d, r));
            return adapter.get();
        };

        this.device = new MockInputDevice("mock", adapterSupplier);
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
        assertThrows(IllegalStateException.class,
                () -> device.initAdapter());
    }

    @Test
    void registerFields() {
        /*
         * The MockInputDevice constructed in setup() uses the default
         * arguments. As such, it should have registered the FEATURE
         * field which is present in the MockInputDevice class and is
         * annotated with @FeaturePresent.
         */
        assertTrue(device.isRegistered(MockInputDevice.FEATURE));

        /*
         * Attempting to call registerFields() again should result in an
         * IllegalStateException being thrown. All fields marked with the
         * @FeaturePresent annotation have already been combed over.
         */
        assertThrows(IllegalStateException.class,
                () -> device.registerFields());

        /*
         * It makes no sense for a feature field annotated with the
         * @FeaturePresent annotation to be private (as it would not
         * be accessible to the outside world.) Furthermore, if the
         * field is not assignable from the DeviceFeature class, then
         * it is not even a device feature in the first place.
         */
        assertThrows(InputException.class,
                MockInputDevice.WithPrivateFeature::new);
        assertThrows(InputException.class,
                MockInputDevice.WithUnassignableFeature::new);
    }

    @Test
    void getFeatures() {
        /*
         * The getFeatures() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertEquals(device.getFeatures(),
                adapter.registry.getFeatures());
    }

    @Test
    void getRegistered() {
        /*
         * The getRegistered() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertEquals(device.getRegistered(MockInputDevice.FEATURE),
                adapter.registry.getRegistered(MockInputDevice.FEATURE));
    }

    @Test
    void isRegistered() {
        MockDeviceFeature feature = new MockDeviceFeature();

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
        MockDeviceFeature feature = new MockDeviceFeature();
        AtomicBoolean registered = new AtomicBoolean();
        device.onRegisterFeature((f) -> registered.set(f == feature));

        device.registerFeature(feature);
        assertTrue(registered.get());
        assertTrue(device.isRegistered(feature));

        /*
         * It makes no sense to register a null feature or a feature which
         * has already been registered. As such, assume these were mistakes
         * on the side of the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.registerFeature(null));
        assertThrows(IllegalStateException.class,
                () -> device.registerFeature(feature));
    }

    @Test
    void unregisterFeature() {
        MockDeviceFeature feature = new MockDeviceFeature();
        AtomicBoolean unregistered = new AtomicBoolean();
        device.onUnregisterFeature((f) -> unregistered.set(f == feature));
        device.registerFeature(feature); /* something to unregister */

        device.unregisterFeature(feature);
        assertTrue(unregistered.get());
        assertFalse(device.isRegistered(feature));

        /*
         * It makes no sense to unregister a null feature or a feature which
         * has already been unregistered. As such, assume these were mistakes
         * on the side of the user and throw an exception.
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
    }

}