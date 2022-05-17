package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class ContainerStateTest {

    private Object internalState;
    private MockContainerState container;

    @BeforeEach
    void createState() {
        this.internalState = new Object();
        this.container = new MockContainerState(internalState);
    }

    @Test
    void testInit() {
        /*
         * It would not make sense for a state container to contain a null
         * internal state. As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockContainerState(null));
    }

    @Test
    void ensureSameState() {
        assertSame(internalState, container.internalState);
    }

}
