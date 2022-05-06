package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LedStateTest {

    private LedState state;

    @BeforeEach
    void createState() {
        this.state = new LedState();
    }

    @Test
    void testGetMode() {
        assertEquals(LedState.MODE_NUMBER, state.getMode());
    }

    @Test
    void testGetValue() {
        assertEquals(1, state.getValue());
    }

    @Test
    void testSetNumber() {
        state.setNumber(4);
        assertEquals(4, state.getValue());
        assertEquals(LedState.MODE_NUMBER, state.getMode());
    }

    @Test
    void testSetPattern() {
        state.setPattern(0b1111);
        assertEquals(0b1111, state.getValue());
        assertEquals(LedState.MODE_PATTERN, state.getMode());
    }

}
