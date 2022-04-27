package io.ketill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
class StateContainerTest {

    @Test
    void __init__() {
        /*
         * It would not make sense for a state container to contain a null
         * internal state. As such, assume this was a mistake by the user
         * and throw an exception.
         */
        assertThrows(NullPointerException.class,
                () -> new MockStateContainer(null));
    }

    private Object internalState;
    private MockStateContainer container;

    @BeforeEach
    void setup() {
        this.internalState = new Object();
        this.container = new MockStateContainer(internalState);
    }

    @Test
    void internalState() {
        assertSame(internalState, container.internalState);
    }

}
