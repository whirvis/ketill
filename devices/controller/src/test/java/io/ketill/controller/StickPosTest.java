package io.ketill.controller;

import io.ketill.IoDeviceObserver;
import org.joml.Vector3f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StickPosTest {

    private AnalogStick stick;
    private AnalogStickCalibration calibration;
    private Controller controller;
    private IoDeviceObserver observer;
    private StickPosZ internal;
    private StickPos container;

    /* @formatter:off */
    private void assertEmittedPress(Direction direction) {
        verify(observer).onNext(argThat(matcher -> {
            if (!(matcher instanceof AnalogStickPressEvent)) {
                return false;
            }
            AnalogStickPressEvent event = (AnalogStickPressEvent) matcher;
            return event.getController() == controller
                    && event.getStick() == stick
                    && event.getDirection() == direction;
        }));
    }
    /* @formatter:on */

    @BeforeEach
    void createState() {
        this.stick = new AnalogStick("stick");
        this.calibration = mock(AnalogStickCalibration.class);

        this.controller = mock(Controller.class);
        this.observer = mock(IoDeviceObserver.class);
        when(observer.getDevice()).thenReturn(controller);

        this.internal = new StickPosZ(stick, observer, calibration);
        this.container = new StickPos(internal);
    }

    @Test
    void testUseCalibration() {
        container.useCalibration(null);
        assertNull(container.getCalibration());
        container.useCalibration(calibration);
        assertSame(calibration, container.getCalibration());
    }

    @Test
    void testGetCalibration() {
        assertSame(calibration, container.getCalibration());
    }

    @Test
    void testGetPos() {
        Vector3f raw = new Vector3f(1.23F, 4.56F, 7.89F);
        internal.pos.set(raw);
        assertEquals(raw, container.getPos(false));

        Vector3f calibrated = new Vector3f(10.11F, 12.13F, 14.15F);
        internal.calibratedPos.set(calibrated);
        assertEquals(calibrated, container.getPos());
    }

    @Test
    void testGetX() {
        internal.pos.x = 1.23F;
        assertEquals(1.23F, container.getX(false));
        internal.calibratedPos.x = 4.56F;
        assertEquals(4.56F, container.getX());
    }

    @Test
    void testGetY() {
        internal.pos.y = 1.23F;
        assertEquals(1.23F, container.getY(false));
        internal.calibratedPos.y = 4.56F;
        assertEquals(4.56F, container.getY());
    }

    @Test
    void testGetZ() {
        internal.pos.z = 1.23F;
        assertEquals(1.23F, container.getZ(false));
        internal.calibratedPos.z = 4.56F;
        assertEquals(4.56F, container.getZ());
    }

    @Test
    void testUpdate() {
        internal.update(); /* trigger calibration */
        verify(calibration).applyTo((Vector3f) any());

        internal.pos.y = 1.0F; /* facing up */
        internal.update(); /* trigger event emission */
        assertEmittedPress(Direction.UP);

        internal.pos.y = -1.0F; /* facing down */
        internal.update(); /* trigger event emission */
        assertEmittedPress(Direction.DOWN);

        internal.pos.x = -1.0F; /* facing left */
        internal.update(); /* trigger event emission */
        assertEmittedPress(Direction.LEFT);

        internal.pos.x = 1.0F; /* facing right */
        internal.update(); /* trigger event emission */
        assertEmittedPress(Direction.RIGHT);
    }

}
