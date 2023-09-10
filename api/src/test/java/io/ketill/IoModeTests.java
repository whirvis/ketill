package io.ketill;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public final class IoModeTests {

    @Test
    void modesAreNotUseless() {
        for (IoMode value : IoMode.values()) {
            assertTrue(value.isRead() || value.isWrite());
        }
    }

    @Test
    void readModesSupportsRead() {
        assertTrue(IoMode.READ.isRead());
        assertTrue(IoMode.READ_WRITE.isRead());
    }

    @Test
    void readOnlyModesDoNotSupportWrite() {
        assertFalse(IoMode.READ.supports(IoMode.WRITE));
        assertFalse(IoMode.READ.supports(IoMode.READ_WRITE));
    }

    @Test
    void writeModesSupportsWrite() {
        assertTrue(IoMode.WRITE.isWrite());
        assertTrue(IoMode.READ_WRITE.isWrite());
    }

    @Test
    void writeOnlyModesDoNotSupportRead() {
        assertFalse(IoMode.WRITE.supports(IoMode.READ));
        assertFalse(IoMode.WRITE.supports(IoMode.READ_WRITE));
    }

    @Test
    void modesDoNotSupportNull() {
        for (IoMode mode : IoMode.values()) {
            assertFalse(mode.supports(null));
        }
    }

    @Test
    void modesSupportThemselves() {
        for (IoMode mode : IoMode.values()) {
            assertTrue(mode.supports(mode));
        }
    }

    @Test
    void modesHaveAtLeastOneId() {
        for (IoMode value : IoMode.values()) {
            assertTrue(value.ids().length > 0);
        }
    }

    @Test
    void idsAreNotNull() {
        for (IoMode value : IoMode.values()) {
            for (String id : value.ids()) {
                assertNotNull(id);
            }
        }
    }

    @Test
    void idsHaveNoWhitespace() {
        Pattern nonWhitespacePattern = Pattern.compile("\\S+");
        for (IoMode value : IoMode.values()) {
            for (String id : value.ids()) {
                Matcher matcher = nonWhitespacePattern.matcher(id);
                assertTrue(matcher.matches(), "IDs must have no whitespace");
            }
        }
    }

    @Test
    void idsAreNotBlank() {
        for (IoMode value : IoMode.values()) {
            for (String id : value.ids()) {
                assertFalse(id.trim().isEmpty(), "IDs must not be blank");
            }
        }
    }

    @Test
    void idsAreUniqueAndConsistent() {
        Set<String> ids = new HashSet<>();
        for (IoMode value : IoMode.values()) {
            for (String id : value.ids()) {
                assertTrue(ids.add(id), "IDs must be unique");
                assertSame(value, IoMode.of(id));
            }
        }
    }

    @Test
    void illegalIdsCauseExceptions() {
        assertThrows(NullPointerException.class, () -> IoMode.of(null));
        assertThrows(IllegalArgumentException.class, () -> IoMode.of(""));
    }

}
