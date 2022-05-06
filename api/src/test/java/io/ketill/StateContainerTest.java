package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class StateContainerTest {

    private Object internalState;
    private MockStateContainer container;

    @BeforeEach
    void createState() {
        this.internalState = new Object();
        this.container = new MockStateContainer(internalState);
    }

    @Test
    void testInit() {
        /*
         * It would not make sense for a state container to contain a null
         * internal state. As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockStateContainer(null));
    }

    @Test
    void ensureSameState() {
        assertSame(internalState, container.internalState);
    }

}
