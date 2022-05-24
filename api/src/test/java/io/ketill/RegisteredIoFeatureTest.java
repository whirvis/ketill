package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisteredIoFeatureTest {

    private MockIoFeature feature;
    private IoDeviceObserver observer;
    private RegisteredIoFeature<?, ?, ?> registered;

    @BeforeEach
    void createRegistration() {
        IoDevice device = mock(IoDevice.class);
        IoDeviceObserver observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(device);

        this.feature = new MockIoFeature();
        feature.containerState = new Object();
        this.observer = observer;
        this.registered = new RegisteredIoFeature<>(feature, observer);
    }

    @Test
    void testInit() {
        /*
         * Since the registered feature created for other tests was created
         * with a feature with a non-autonomous state, it should have set
         * its autonomous updater to the no-op runnable.
         */
        assertSame(registered.autonomousUpdater, RegisteredIoFeature.NO_UPDATER);

        /* use autonomous state for next test */
        AutonomousState autonomousState = mock(AutonomousState.class);
        feature.internalState = autonomousState;

        /*
         * Since this registered feature was created for a feature with a now
         * autonomous state, its autonomous updater should not be the same as
         * the no-op runnable used for other internal states.
         */
        RegisteredIoFeature<?, ?, ?> autonomousRegistered =
                new RegisteredIoFeature<>(feature, observer);
        assertNotSame(autonomousRegistered.autonomousUpdater,
                RegisteredIoFeature.NO_UPDATER);

        /*
         * When invoked, the autonomous updater should invoke the update()
         * method found within the AutonomousState interface.
         */
        autonomousRegistered.autonomousUpdater.run();
        verify(autonomousState).update();
    }

    @Test
    void testGetFeature() {
        assertSame(feature, registered.getFeature());
    }

    @Test
    void testGetState() {
        assertSame(feature.containerState, registered.getState());
    }

}
