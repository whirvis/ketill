package io.ketill.xinput;

import io.ketill.MappedFeatureRegistry;
import io.ketill.xbox.XboxController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisabledOnOs(OS.WINDOWS)
class XInputUnavailableTest {

    @BeforeEach
    void setup() {
        assertFalse(XInputStatus.isAvailable());
        assertThrows(XInputException.class, XInputStatus::requireAvailable);
    }

    /**
     * Since X-input is unavailable on this system, the XInputXboxSeeker
     * class is expected to throw an exception at construction. There is
     * nothing it can do if it can't use X-input!
     */
    @Test
    void createSeeker() {
        assertThrows(XInputException.class, XInputXboxSeeker::new);
    }

    /**
     * Since X-input is unavailable on this system, the XInputXboxAdapter
     * class is expected to throw an exception at construction. Since the
     * check for the presence of X-input occurs before validating xDevice
     * is not null, this test works as expected.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    void createAdapter() {
        XboxController controller = mock(XboxController.class);
        MappedFeatureRegistry registry = mock(MappedFeatureRegistry.class);
        assertThrows(XInputException.class,
                () -> new XInputXboxAdapter(controller, registry, null));
    }

}
