package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class MappedFeatureRegistryTest {

    private MappedFeatureRegistry registry;

    @BeforeEach
    void setup() {
        this.registry = new MappedFeatureRegistry();
    }

    @Test
    void hasMapping() {
        /*
         * It makes no sense for a null feature to be mapped. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> registry.hasMapping(null));
    }

    @Test
    void mapFeature() {
        MockIoFeature feature = new MockIoFeature();
        AtomicBoolean updated = new AtomicBoolean();

        /* feature must be registered for updates */
        registry.registerFeature(feature);

        /*
         * Ensure that the base mapFeature() method passes the correct
         * parameters when updating the feature. This is verified via
         * a randomly generated value on each test.
         */
        updated.set(false);
        int paramsValue = new Random().nextInt();
        registry.mapFeature(feature, paramsValue,
                (s, p) -> updated.set(p == paramsValue));
        assertTrue(registry.hasMapping(feature));
        registry.updateFeatures();
        assertTrue(updated.get());

        /*
         * When mapping a feature without any specified parameters, but
         * still providing an updater which takes in a parameter, the
         * received parameter must be the feature itself.
         */
        updated.set(false);
        registry.mapFeature(feature, (s, p) -> updated.set(p == feature));
        registry.updateFeatures();
        assertTrue(updated.get());

        /*
         * Mapping a feature with an updater that takes in no parameters
         * is allowed via a shorthand. Ensure that this shorthand works
         * by verifying that the feature was updated.
         */
        updated.set(false);
        registry.mapFeature(feature, (s) -> updated.set(true));
        registry.updateFeatures();
        assertTrue(updated.get());

        /*
         * A null value is allowed for the parameters when mapping a feature.
         * While not recommended (StateUpdater.NoParams should be used in
         * this scenario), it is still not illegal. As such, no exception
         * should be thrown here.
         */
        assertDoesNotThrow(() -> registry.mapFeature(feature, null, (f, s) -> {
        }));

        /*
         * It makes no sense to map a null feature or map a feature to a
         * null updater. As such, assume these were mistakes by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> registry.mapFeature(null, null, (f, s) -> {
        }));
        assertThrows(NullPointerException.class,
                () -> registry.mapFeature(feature, feature, null));
    }

    @Test
    void unmapFeature() {
        AtomicBoolean updated = new AtomicBoolean();
        MockIoFeature feature = new MockIoFeature();

        /* feature must be registered for updates */
        RegisteredFeature<?, ?> registeredFeature =
                registry.registerFeature(feature);

        /*
         * The unmapFeature() method should return false unless it has
         * actually unmapped a feature from an updater.
         */
        assertFalse(registry.unmapFeature(feature));
        registry.mapFeature(feature, (s) -> updated.set(true));
        assertTrue(registry.unmapFeature(feature));
        assertFalse(registry.hasMapping(feature));

        /*
         * After a feature has been unmapped, its updater should be reassigned
         * to a no-op. For this test, that would mean the value of updated is
         * expected to remain false. If it is set to true, then unmapFeature()
         * has not properly unmapped the feature.
         */
        assertSame(registeredFeature.updater, RegisteredFeature.NO_UPDATER);
        registry.updateFeatures();
        assertFalse(updated.get());

        /*
         * It makes no sense to unmap a null feature. As such, assume
         * this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> registry.unmapFeature(null));
    }

    @Test
    void getFeatures() {
        /*
         * The getFeatures() method provides a read-only view of all registered
         * features in a feature registry. Ensure that it never returns null
         * (even when it is empty) and that is unmodifiable from the outside.
         */
        Collection<RegisteredFeature<?, ?>> features = registry.getFeatures();
        assertNotNull(features); /* this should never be null, only empty */
        assertThrows(UnsupportedOperationException.class, features::clear);
    }

    @Test
    void getFeatureRegistration() {
        MockIoFeature feature = new MockIoFeature();
        assertNull(registry.getFeatureRegistration(feature));

        /*
         * The getRegistered() method returns the instance of an earlier
         * registered feature. As such, it should return the same value
         * as the registerFeature() method.
         */
        RegisteredFeature<?, ?> registeredFeature =
                registry.registerFeature(feature);
        assertSame(registeredFeature, registry.getFeatureRegistration(feature));

        /*
         * It makes no sense to get the registration of a null feature. As
         * such, assume this was a mistake by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> registry.getFeatureRegistration(null));
    }

    @Test
    void getState() {
        MockIoFeature feature = new MockIoFeature();

        /*
         * It makes no sense to retrieve the state of a null feature or the
         * state of a feature that is not yet registered. As such, assume
         * these were mistakes by the user and throw an exception.
         */
        assertThrows(NullPointerException.class, () -> registry.getState(null));
        assertThrows(IllegalStateException.class,
                () -> registry.getState(feature));

        /*
         * The value of state inside registeredField must match the
         * value returned by getState(), as it is a shorthand.
         */
        RegisteredFeature<?, ?> registeredFeature =
                registry.registerFeature(feature);
        assertSame(registeredFeature.state, registry.getState(feature));
    }

    @Test
    void requestState() {
        MockIoFeature feature = new MockIoFeature();

        /*
         * It makes no sense to retrieve the state of a null feature.
         * As such, assume this was a mistake by the user and throw
         * an exception.
         */
        assertThrows(NullPointerException.class, () -> registry.getState(null));
        assertThrows(IllegalStateException.class,
                () -> registry.getState(feature));

        /*
         * Unlike getState(), requestState() simply returns null if
         * the feature is not currently registered.
         */
        assertNull(registry.requestState(feature));
    }

    @Test
    void registerFeature() {
        /* it is convient to test isRegistered() here */
        MockIoFeature feature = new MockIoFeature();
        assertFalse(registry.isFeatureRegistered(feature));
        RegisteredFeature<?, ?> registeredFeature =
                registry.registerFeature(feature);
        assertTrue(registry.isFeatureRegistered(feature));

        /*
         * Ensure that the fields within registeredFeature correlate to their
         * expected values. That being the contained feature, the state, as
         * well as the updater. If these are not their expected values, then
         * registerFeature() did not instantiate registeredFeature correctly.
         */
        assertSame(registeredFeature.feature, feature);
        assertInstanceOf(registeredFeature.state.getClass(),
                feature.initialState.get());
        assertSame(registeredFeature.updater, RegisteredFeature.NO_UPDATER);

        /*
         * It makes no sense to register a null feature or a feature which
         * has already been registered. As such, assume these were mistakes
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> registry.registerFeature(null));
        assertThrows(IllegalStateException.class,
                () -> registry.registerFeature(feature));
    }

    @Test
    void unregisterFeature() {
        MockIoFeature feature = new MockIoFeature();
        registry.registerFeature(feature); /* required to unregister */

        registry.unregisterFeature(feature);
        assertFalse(registry.isFeatureRegistered(feature));

        /*
         * It makes no sense to unregister a null feature or a feature which
         * has already been unregistered. As such, assume these were mistakes
         * by the user and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> registry.unregisterFeature(null));
        assertThrows(IllegalStateException.class,
                () -> registry.unregisterFeature(feature));
    }

}