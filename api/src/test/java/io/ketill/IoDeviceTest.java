package io.ketill;

import io.reactivex.rxjava3.disposables.Disposable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static io.ketill.KetillAssertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class IoDeviceTest {

    private MockIoDevice device;
    private MockIoDeviceAdapter adapter;

    @BeforeEach
    void createDevice() {
        AtomicReference<MockIoDeviceAdapter> adapter = new AtomicReference<>();
        AdapterSupplier<MockIoDevice> adapterSupplier = (d, r) -> {
            adapter.set(new MockIoDeviceAdapter(d, r));
            return adapter.get();
        };

        this.device = new MockIoDevice("mock", adapterSupplier);
        this.adapter = adapter.get();
    }

    @Test
    void testInit() {
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

    @Test
    void ensureRegistryMethodsFinal() {
        // todo
    }

    @Test
    void testGetTypeId() {
        assertEquals("mock", device.getTypeId());
    }

    @Test
    void testSubscribeEvents() {
        /*
         * It makes no sense to subscribe for events of a null type or to
         * subscribe for events with a null callback. As such, assume this
         * was a mistake by the user and throw an exception.
         */
        /* @formatter:off */
        assertThrows(NullPointerException.class,
                () -> device.subscribeEvents(null, event -> {}));
        assertThrows(NullPointerException.class,
                () -> device.subscribeEvents(null));
        /* @formatter:on */

        AtomicBoolean emitted = new AtomicBoolean();
        Disposable subscription = device.subscribeEvents(event -> {
            IoDevice emitter = event.getDevice();
            emitted.set(emitter == device);
        });

        /*
         * Once subscribed, the I/O device should return a subscription that
         * can later be disposed of. Furthermore, since no type was given,
         * the argument for the eventClazz parameter should have defaulted
         * to IoDeviceEvent.class. As such, any emitted events should result
         * in the callback being executed.
         */
        assertNotNull(subscription);
        device.observer.onNext(new MockIoDeviceEvent(device));
        assertTrue(emitted.get());

        /* reset emitted for next test */
        emitted.set(false);

        /*
         * After the subscription has been disposed of, the callback should
         * no longer be executed.
         */
        subscription.dispose();
        device.observer.onNext(new MockIoDeviceEvent(device));
        assertFalse(emitted.get());
    }

    @Test
    void testInitAdapter() {
        /*
         * The MockInputDevice constructed in setup() uses the default
         * arguments. As such, it should have initialized its adapter.
         */
        assertTrue(adapter.isInitialized());

        /*
         * For the next test, a device must be created which does not
         * automatically initialize its adapter. There must also be a
         * subscriber before it is manually initialized.
         */
        MockIoDevice uninitialized = new MockIoDevice("uninitialized",
                MockIoDeviceAdapter::new, false, false);
        AtomicBoolean initialized = new AtomicBoolean();
        uninitialized.subscribeEvents(AdapterInitializedEvent.class,
                event -> initialized.set(true));

        uninitialized.initAdapter();
        assertTrue(device.adapterInitialized);
        assertTrue(initialized.get());

        /*
         * Attempting to call initAdapter() again should result in an
         * IllegalStateException being thrown. Initializing the adapter
         * twice makes no sense, and would lead to bugs.
         */
        assertThrows(IllegalStateException.class, device::initAdapter);
    }

    @Test
    void testRegisterFields() {
        /*
         * The MockInputDevice constructed in setup() uses the default
         * arguments. As such, it should have registered the FEATURE
         * field which is present in the MockInputDevice class and is
         * annotated with @FeaturePresent.
         */
        assertTrue(device.isFeatureRegistered(MockIoDevice.FEATURE));

        /*
         * For the next test, a device must be created which does not
         * automatically register fields annotated with FeaturePresent.
         * There must also be a subscriber before registering them.
         */
        MockIoDevice unregistered = new MockIoDevice("unregistered",
                MockIoDeviceAdapter::new, false, false);
        AtomicBoolean registered = new AtomicBoolean();
        unregistered.subscribeEvents(FieldsRegisteredEvent.class,
                event -> registered.set(true));

        unregistered.registerFields();
        assertTrue(device.fieldsRegistered);
        assertTrue(registered.get());

        /*
         * Attempting to call registerFields() again should result in an
         * IllegalStateException being thrown. All fields marked with the
         * @FeaturePresent annotation have already been combed over.
         */
        assertThrows(IllegalStateException.class, device::registerFields);

        /*
         * It makes no sense for fields annotated with @FeaturePresent to
         * be private (as it would not be accessible to the outside world.)
         * Furthermore, if the field is not assignable from IoFeature, then
         * it cannot be registered as an I/O feature.
         */
        assertThrows(KetillException.class,
                MockIoDevice.WithPrivateFeature::new);
        assertThrows(KetillException.class,
                MockIoDevice.WithUnassignableFeature::new);
    }

    @Test
    void testGetFeature() {
        /*
         * It does not make sense to get a feature from a null container
         * state. As such, assume this was a mistake by the user and throw
         * an exception.
         */
        assertThrows(NullPointerException.class,
                () -> device.getFeatureById(null));

        /*
         * This method is to get the feature which created the given state,
         * not the other way around. Assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(KetillException.class,
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
    void testIsFeatureSupported() {
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
    void testIsFeatureRegistered() {
        MockIoFeature feature = new MockIoFeature("test");

        /*
         * The isRegistered() method is an accessor to isRegistered() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertEquals(device.isFeatureRegistered(feature),
                adapter.registry.isFeatureRegistered(feature));
        device.registerFeature(feature);
        assertEquals(device.isFeatureRegistered(feature),
                adapter.registry.isFeatureRegistered(feature));
    }

    @Test
    void testIsFeatureWithIdRegistered() {
        MockIoFeature feature = new MockIoFeature("test");
        String featureId = feature.getId();

        /*
         * The isFeatureWithIdRegistered() method is an accessor to
         * isFeatureWithIdRegistered() in MappedFeatureRegistry. As such,
         * their results should be equal.
         */
        assertEquals(device.isFeatureWithIdRegistered(featureId),
                adapter.registry.isFeatureWithIdRegistered(featureId));
        device.registerFeature(feature);
        assertEquals(device.isFeatureWithIdRegistered(featureId),
                adapter.registry.isFeatureWithIdRegistered(featureId));
    }

    @Test
    void testGetFeatureCount() {
        /*
         * Since a single feature is declared and annotated with the
         * FeaturePresent annotation in the MockIoFeature class, this
         * should return a value of one.
         */
        assertEquals(1, device.getFeatureCount());
    }

    @Test
    void testGetFeatureById() {
        String featureId = MockIoDevice.FEATURE.getId();

        /*
         * The getFeatureById() method is an accessor to getFeatureById()
         * in MappedFeatureRegistry. As such, their results should be of
         * the same instance.
         */
        assertSame(device.getFeatureById(featureId),
                adapter.registry.getFeatureById(featureId));
    }

    @Test
    void testGetFeatures() {
        /*
         * The getFeatures() method is an accessor to getFeatures() in
         * MappedFeatureRegistry. As such, their results should be equal.
         */
        assertIterableEquals(device.getFeatures(),
                adapter.registry.getFeatures());
    }

    @Test
    void testGetFeatureRegistration() {
        /*
         * The getFeatureRegistration() method in IoDevice is an accessor
         * method to the same method implemented in MappedFeatureRegistry.
         * As such, their results should be of the same instance.
         */
        assertSame(device.getFeatureRegistration(MockIoDevice.FEATURE),
                adapter.registry.getFeatureRegistration(MockIoDevice.FEATURE));
    }

    @Test
    void testGetFeatureRegistrations() {
        /*
         * The getFeatureRegistrations() method is an accessor to
         * getFeatureRegistrations() in MappedFeatureRegistry. As such,
         * their results should be equal.
         */
        assertIterableEquals(device.getFeatureRegistrations(),
                adapter.registry.getFeatureRegistrations());
    }

    @Test
    void testGetState() {
        /*
         * The getState() method in IoDevice is an accessor method to the
         * same method implemented in MappedFeatureRegistry. As a result,
         * their results should be of the same instance.
         */
        assertSame(device.getState(MockIoDevice.FEATURE),
                adapter.registry.getState(MockIoDevice.FEATURE));
    }

    @Test
    void testGetInternalState() {
        /*
         * The getInternalState() method in IoDevice is an accessor method
         * to the same method implemented in MappedFeatureRegistry. As such,
         * their results should be of the same instance.
         */
        assertSame(device.getInternalState(MockIoDevice.FEATURE),
                adapter.registry.getInternalState(MockIoDevice.FEATURE));
    }

    @Test
    void testRegisterFeature() {
        MockIoFeature feature = new MockIoFeature("test");
        AtomicBoolean registered = new AtomicBoolean();
        device.subscribeEvents(IoFeatureRegisterEvent.class,
                event -> registered.set(event.getFeature() == feature));

        device.registerFeature(feature);
        assertTrue(device.featureRegistered);
        assertTrue(registered.get());
        assertTrue(device.isFeatureRegistered(feature));

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
    void testUnregisterFeature() {
        MockIoFeature feature = new MockIoFeature("test");
        AtomicBoolean unregistered = new AtomicBoolean();
        device.subscribeEvents(IoFeatureUnregisterEvent.class,
                event -> unregistered.set(event.getFeature() == feature));

        /* register feature for next test */
        device.registerFeature(feature);

        device.unregisterFeature(feature);
        assertTrue(device.featureUnregistered);
        assertTrue(unregistered.get());
        assertFalse(device.isFeatureRegistered(feature));

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
    void testIsConnected() {
        AtomicBoolean connected = new AtomicBoolean();
        device.subscribeEvents(IoDeviceConnectEvent.class,
                event -> connected.set(true));
        device.subscribeEvents(IoDeviceDisconnectEvent.class,
                event -> connected.set(false));

        /*
         * First test the event for device connection. Since the device is
         * disconnected when first instantiated, it will not emit any events.
         * Connecting the device here, then disconnecting it, will have the
         * disconnection event emitted as desired.
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
    }

    @Test
    void testPollError() {
        adapter.errorOnPoll = true;

        /*
         * When an error occurs while polling the device adapter, the device
         * is obligated to wrap the exception throw it back to the caller.
         * This ensures errors do not occur silently.
         */
        assertThrows(KetillException.class, device::poll);
    }

    @Test
    void ensureImplementsToString() {
        assertImplementsToString(IoDevice.class, device);
    }

}