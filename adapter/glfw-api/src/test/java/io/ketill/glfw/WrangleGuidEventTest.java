package io.ketill.glfw;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
class WrangleGuidEventTest {

    private GlfwJoystickSeeker<?> seeker;
    private String guid;
    private GlfwJoystickWrangler<?> wrangler;
    private WrangleGuidEvent event;

    @BeforeEach
    void createEvent() {
        this.seeker = mock(GlfwJoystickSeeker.class);
        this.guid = "0123456789abcdef";
        this.wrangler = mock(GlfwJoystickWrangler.class);
        this.event = new WrangleGuidEvent(seeker, guid, wrangler);
    }

    @Test
    void testInit() {
        /*
         * This event is emitted when a GUID is wrangled. It would not make
         * sense in this context for the GUID or the wrangler to be null.
         * As such, assume this was a mistake and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new WrangleGuidEvent(seeker, null, wrangler));
        assertThrows(NullPointerException.class,
                () -> new WrangleGuidEvent(seeker, guid, null));
    }

    @Test
    void testGetGuid() {
        assertSame(guid, event.getGuid());
    }

    @Test
    void testGetWrangler() {
        assertSame(wrangler, event.getWrangler());
    }

}
