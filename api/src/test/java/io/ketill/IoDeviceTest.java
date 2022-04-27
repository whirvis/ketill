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
        /* @formatter:off */
        assertThrows(NullPointerException.class,
                () -> new MockIoDevice(null, MockIoDeviceAdapter::new));
        assertThrows(NullPointerException.class,
                () -> new MockIoDevice("mock", null));
        assertThrows(NullPointerException.class,
                () -> new MockIoDevice("mock", (d, r) -> null));
        /* @formatter:on */

        /*
         * It makes no sense for the input device's ID to be blank.
         * Furthermore, any whitespace in an ID is illegal.
         */
        /* @formatter:off */
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoDevice("", MockIoDeviceAdapter::new));
        assertThrows(IllegalArgumentException.class,
                () -> new MockIoDevice("\t", MockIoDeviceAdapter::new));
        /* @formatter:on */

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
        MockIoDevice device = new MockIoDevice("mock", adapterSupplier,
                false, false);
        assertFalse(device.isFeatureRegistered(MockIoDevice.FEATURE));
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
        assertTrue(device.isFeatureRegistered(MockIoDevice.FEATURE));

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
    void getFeature() {
        /*
         * It does not make sense to get a feature from a null container
         * state. As such, assume this was a mistake by the user and throw
         * an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.getFeature(null));

        /*
         * This method is to get the feature which created the given state,
         * not the other way around. Assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(UnsupportedOperationException.class,
                () -> device.getFeature(MockIoDevice.FEATURE));

        /*
         * When getting the feature from a state which is belongs to no
         * feature registered to the device, a value of null should be
         * returned. Do not throw an exception.
         */
        assertNull(device.getFeature(new Object()));

        /*
         * When getting the feature from its internal state or container
         * state, the feature returned should match the one which owns the
         * state. Otherwise, this method is broken.
         */
        assertSame(MockIoDevice.FEATURE,
                device.getFeature(device.featureInternalState));
        assertSame(MockIoDevice.FEATURE,
                device.getFeature(device.featureContainerState));
    }

    @Test
    void isFeatureSupported() {
        /*
         * It would not make sense to check if a null feature is supported.
         * As such, assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.isFeatureSupported(null));
        assertThrows(NullPointerException.class,
                () -> device.isFeatureSupported((Object) null));

        /*
         * This version of the method checks if a feature is supported by
         * taking in the actual feature. It's a bit more direct, as all it
         * needs to do is see if a mapping for it exists in the internal
         * mapped feature registry. This should not throw an exception for
         * features which are not registered to the device.
         */
        assertTrue(device.isFeatureSupported(MockIoDevice.FEATURE));
        assertFalse(device.isFeatureSupported(new MockIoFeature()));

        /*
         * This version of the method is a bit more complicated. This works
         * by getting the feature associated with the specified state. Once
         * the feature is retrieved, it then calls the original method which
         * takes in a feature. No exception should be thrown if the feature
         * owning the specified container state cannot be identified.
         */
        assertTrue(device.isFeatureSupported(device.featureInternalState));
        assertTrue(device.isFeatureSupported(device.featureContainerState));
        assertFalse(device.isFeatureSupported(new Object()));
    }

    @Test
    void isFeatureRegistered() {
        /*
         * The isRegistered() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        MockIoFeature feature = new MockIoFeature();
        assertEquals(device.isFeatureRegistered(feature),
                adapter.registry.isFeatureRegistered(feature));
        device.registerFeature(feature);
        assertEquals(device.isFeatureRegistered(feature),
                adapter.registry.isFeatureRegistered(feature));
    }

    @Test
    void getFeatureCount() {
        /*
         * Since a single feature is declared and annotated with
         * @FeaturePresent in the MockIoFeature class, this should
         * return a value of one.
         */
        assertEquals(1, device.getFeatureCount());
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
    void getFeatureRegistration() {
        /*
         * The getFeatureRegistration() method in IoDevice is an accessor
         * method to the same method implemented in MappedFeatureRegistry.
         * As such, their results should be equal.
         */
        assertSame(device.getFeatureRegistration(MockIoDevice.FEATURE),
                adapter.registry.getFeatureRegistration(MockIoDevice.FEATURE));
    }

    @Test
    void getState() {
        /*
         * The getState() method in IoDevice is an accessor method to the
         * same method implemented in MappedFeatureRegistry. As a result,
         * their results should be equal.
         */
        assertSame(device.getState(MockIoDevice.FEATURE),
                adapter.registry.getState(MockIoDevice.FEATURE));
    }

    @Test
    void getInternalState() {
        /*
         * It would not make sense to get the internal state of a null
         * feature or a feature which has not yet been registered. As
         * such, assume this was a user mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.getInternalState(null));
        assertThrows(IllegalStateException.class,
                () -> device.getInternalState(new MockIoFeature()));

        /*
         * When the internal state of a feature is fetched, it should be
         * the same internal state contained in the feature registration.
         */
        Object internalState = device.getInternalState(MockIoDevice.FEATURE);
        RegisteredFeature<?, ?, ?> registered =
                device.getFeatureRegistration(MockIoDevice.FEATURE);
        assertSame(internalState, registered.internalState);
    }

    @Test
    void registerFeature() {
        MockIoFeature feature = new MockIoFeature();
        AtomicBoolean registered = new AtomicBoolean();
        device.onRegisterFeature((d, f) -> registered.set(f == feature));

        device.registerFeature(feature);
        assertTrue(device.featureRegistered);
        assertTrue(registered.get());
        assertTrue(device.isFeatureRegistered(feature));

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
        device.onUnregisterFeature((d, f) -> unregistered.set(f == feature));

        /* register feature for next test */
        device.registerFeature(feature);

        device.unregisterFeature(feature);
        assertTrue(device.featureUnregistered);
        assertTrue(unregistered.get());
        assertFalse(device.isFeatureRegistered(feature));

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
        device.onConnect((d) -> connected.set(true));
        device.onDisconnect((d) -> connected.set(false));

        /*
         * First test the callback for when the device is connected. Since
         * the device is disconnected when first instantiated, it will not
         * send a callback. Connecting the device here, then disconnecting
         * it, will have the disconnect callback fired as desired.
         */
        adapter.shouldBeConnected = true;
        device.poll();
        assertTrue(device.deviceConnected);
        assertTrue(connected.get());
        assertTrue(device.isConnected());

        adapter.shouldBeConnected = false;
        device.poll();
        assertTrue(device.deviceDisconnected);
        assertFalse(connected.get());
        assertFalse(device.isConnected());

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the device.
         */
        assertDoesNotThrow(() -> device.onConnect(null));
        assertDoesNotThrow(() -> device.onDisconnect(null));
    }

    @Test
    void pollError() {
        adapter.errorOnPoll = true;

        /*
         * When no error callback is set, a device is obligated to wrap
         * the exception it encounters and throw it back. This ensures
         * errors do not occur silently.
         */
        assertThrows(KetillException.class, device::poll);
        assertTrue(device.caughtPollError);

        /* reset state for next test */
        device.caughtPollError = false;

        /*
         * Once an error callback is set, the device must not throw the
         * exception it encounters in seek(). Rather, it must notify the
         * callback of the error that has occurred and pass the exception.
         */
        AtomicBoolean caughtError = new AtomicBoolean();
        device.onPollError((d, e) -> caughtError.set(true));
        assertDoesNotThrow(device::poll);
        assertTrue(device.caughtPollError);
        assertTrue(caughtError.get());

        /*
         * A null value is allowed when setting a callback. This should
         * have the effect of removing the callback from the device.
         */
        assertDoesNotThrow(() -> device.onPollError(null));
    }

}