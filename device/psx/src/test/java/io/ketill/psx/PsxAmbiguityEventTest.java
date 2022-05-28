package io.ketill.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PsxAmbiguityEventTest {

    private PsxAmbiguityEvent event;

    @BeforeEach
    void createEvent() {
        MockPsxSeeker seeker = new MockPsxSeeker();
        this.event = new PsxAmbiguityEvent(seeker, true);
    }

    @Test
    void testIsNowAmbiguous() {
        assertTrue(event.isNowAmbiguous());
    }

}
