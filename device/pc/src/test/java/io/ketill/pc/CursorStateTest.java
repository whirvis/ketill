package io.ketill.pc;

import io.ketill.IoDeviceObserver;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class CursorStateTest {

    private static final Random RANDOM = new Random();

    private IoDeviceObserver observer;
    private CursorStateZ internal;
    private CursorState container;

    @BeforeEach
    void setup() {
        MouseCursor cursor = mock(MouseCursor.class);

        Mouse mouse = mock(Mouse.class);
        this.observer = mock(IoDeviceObserver.class);
        doReturn(mouse).when(observer).getDevice();

        this.internal = new CursorStateZ(cursor, observer);
        this.container = new CursorState(internal);
    }

    @Test
    void testInit() {
        assertThrows(NullPointerException.class, () -> new CursorState(null));
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    void testCanSetVisible() {
        internal.adapterCanSetVisible = true;
        assertTrue(container.canSetVisible());
        internal.adapterCanSetVisible = false;
        assertFalse(container.canSetVisible());
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    void testCanSetPosition() {
        internal.adapterCanSetPosition = true;
        assertTrue(container.canSetPosition());
        internal.adapterCanSetPosition = false;
        assertFalse(container.canSetPosition());
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    void testCanSetIcon() {
        internal.adapterCanSetIcon = true;
        assertTrue(container.canSetIcon());
        internal.adapterCanSetIcon = false;
        assertFalse(container.canSetIcon());
    }

    @SuppressWarnings("UnusedAssignment")
    @Test
    void testIsVisible() {
        internal.visible = true;
        assertTrue(container.isVisible());
        internal.visible = false;
        assertFalse(container.isVisible());
    }

    @Test
    void testSetVisible() {
        AtomicBoolean lastVisibility = new AtomicBoolean();
        doAnswer(answer -> {
            MouseCursorSetVisibilityEvent event = answer.getArgument(0);
            lastVisibility.set(event.isVisible());
            return null;
        }).when(observer).onNext(any());

        /*
         * By default, the internal state assumes that adapter does not have
         * the ability to set the visibility of the mouse cursor. As such,
         * the call below should result in an exception. Afterwards, it will
         * be explicitly enabled for the next tests.
         */
        assertThrows(UnsupportedOperationException.class,
                () -> container.setVisible(true));
        internal.adapterCanSetVisible = true;

        /*
         * Now that the internal state indicates the adapter has the ability
         * to set the visibility of the mouse cursor, testing can continue.
         */
        container.setVisible(true);
        assertTrue(internal.visible);
        container.setVisible(false);
        assertFalse(internal.visible);

        /*
         * When the visibility of the mouse cursor changes, the internal
         * state should emit the corresponding event to subscribers.
         * Note that the cursor must first be made invisible for this test
         * to pass. This is because cursors are visible by default.
         */
        container.setVisible(false);
        internal.update();  /* trigger event emission */
        assertFalse(lastVisibility.get());
        container.setVisible(true);
        internal.update(); /* trigger event emission */
        assertTrue(lastVisibility.get());
    }

    @Test
    void testTrySetVisible() {
        internal.adapterCanSetVisible = true;
        assertTrue(container.trySetVisible(true));
        internal.adapterCanSetVisible = false;
        assertFalse(container.trySetVisible(true));
    }

    @Test
    void testGetPosition() {
        Vector2f currentPos = internal.currentPos;
        currentPos.x = RANDOM.nextFloat();
        currentPos.y = RANDOM.nextFloat();

        Vector2fc pos = container.getPosition();
        assertEquals(currentPos.x, pos.x());
        assertEquals(currentPos.y, pos.y());
    }

    @Test
    void testGetX() {
        Vector2f currentPos = internal.currentPos;
        currentPos.x = RANDOM.nextFloat();
        assertEquals(currentPos.x, container.getX());
    }

    @Test
    void testGetY() {
        Vector2f currentPos = internal.currentPos;
        currentPos.y = RANDOM.nextFloat();
        assertEquals(currentPos.y, container.getY());
    }

    @Test
    void testSetPosition() {
        assertThrows(NullPointerException.class,
                () -> container.setPosition(null));

        /*
         * By default, the internal state assumes that adapter does not have
         * the ability to set the position of the mouse cursor. As such, the
         * call below should result in an exception. Afterwards, it will be
         * explicitly enabled for the next tests.
         */
        assertThrows(UnsupportedOperationException.class,
                () -> container.setPosition(0.0F, 0.0F));
        internal.adapterCanSetPosition = true;

        /*
         * Now that the internal state indicates the adapter has the ability
         * to set the position of the mouse cursor, testing can continue.
         */
        float x = RANDOM.nextFloat();
        float y = RANDOM.nextFloat();
        container.setPosition(x, y);

        /*
         * When setting the position of the mouse, it is the responsibility
         * of the adapter to update the current position. As a result, the
         * current position should be at a zero value.
         */
        Vector2fc pos = container.getPosition();
        assertEquals(0.0F, pos.x());
        assertEquals(0.0F, pos.y());

        assertNotNull(internal.requestedPos);
        assertEquals(x, internal.requestedPos.x());
        assertEquals(y, internal.requestedPos.y());
    }

    @Test
    void testTrySetPosition() {
        internal.adapterCanSetPosition = true;
        assertTrue(container.trySetPosition(0.0F, 0.0F));
        internal.adapterCanSetPosition = false;
        assertFalse(container.trySetPosition(0.0F, 0.0F));
    }

    @Test
    void testGetIcon() {
        internal.icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        assertSame(internal.icon, container.getIcon());
    }

    @Test
    void testSetIcon() {
        AtomicReference<Image> lastIcon = new AtomicReference<>();
        doAnswer(answer -> {
            MouseCursorSetIconEvent event = answer.getArgument(0);
            lastIcon.set(event.getIcon());
            return null;
        }).when(observer).onNext(any());

        /*
         * By default, the internal state assumes that adapter does not have
         * the ability to set the icon of the mouse cursor. As such, the
         * call below should result in an exception. Afterwards, it will
         * be explicitly enabled for the next tests.
         */
        assertThrows(UnsupportedOperationException.class,
                () -> container.setIcon(null));
        internal.adapterCanSetIcon = true;

        /*
         * Now that the internal state indicates the adapter has the ability
         * to set the icon of the mouse cursor, testing can continue.
         */
        BufferedImage img = new BufferedImage(16, 16,
                BufferedImage.TYPE_INT_ARGB);
        container.setIcon(img);

        /*
         * When setting the icon, it is the responsibility of the adapter to
         * update the current cursor icon. However, it must first be notified
         * that there is a new icon to use.
         */
        assertSame(img, container.getIcon());
        assertSame(img, internal.icon);
        assertTrue(internal.updatedIcon);

        /*
         * The container must let the internal state know that there a new
         * icon has just been set. When the internal state is notified of
         * this, it should emit the corresponding event to subscribers.
         */
        assertTrue(internal.emitIconUpdated);
        internal.update(); /* trigger event emission */
        assertSame(img, lastIcon.get());
        assertFalse(internal.emitIconUpdated);
    }

    @Test
    void testTrySetIcon() {
        internal.adapterCanSetIcon = true;
        assertTrue(container.trySetIcon(null));
        internal.adapterCanSetIcon = false;
        assertFalse(container.trySetIcon(null));
    }

    @Test
    void testUpdate() {
        Vector2f lastDisplacement = new Vector2f();
        doAnswer(answer -> {
            MouseCursorDisplaceEvent event = answer.getArgument(0);
            lastDisplacement.set(event.getDisplacement());
            return null;
        }).when(observer).onNext(any());

        /*
         * Each time the mouse cursor position changes from the last update,
         * the cursor state should emit an event to notify listeners. Since
         * the mouse cursor was moved to the right, the displacement should
         * be a positive value.
         */
        internal.currentPos.set(1024.0F, 1024.0F);
        internal.update(); /* trigger event emission */
        assertEquals(1024.0F, lastDisplacement.x());
        assertEquals(1024.0F, lastDisplacement.y());

        /*
         * This time, the mouse cursor was moved to the left of the screen.
         * As such, the displacement should be a negative value.
         */
        internal.currentPos.set(256.0F, 256.0F);
        internal.update(); /* trigger event emission */
        assertEquals(-768.0F, lastDisplacement.x());
        assertEquals(-768.0F, lastDisplacement.y());

        /*
         * Since the mouse cursor has not moved since the last updated,
         * the cursor state should not emit an event to listeners.
         */
        lastDisplacement.set(0.0f, 0.0f);
        internal.update(); /* update without event */
        assertEquals(0.0F, lastDisplacement.x());
        assertEquals(0.0F, lastDisplacement.y());
    }

}
