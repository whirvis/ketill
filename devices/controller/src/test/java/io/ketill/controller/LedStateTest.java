package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LedStateTest {

    private LedState state;

    @BeforeEach
    void setup() {
        this.state = new LedState();
    }

    @Test
    void getMode() {
        assertEquals(LedState.MODE_NUMBER, state.getMode());
    }

    @Test
    void getValue() {
        assertEquals(1, state.getValue());
    }

    @Test
    void setNumber() {
        state.setNumber(4);
        assertEquals(4, state.getValue());
        assertEquals(LedState.MODE_NUMBER, state.getMode());
    }

    @Test
    void setPattern() {
        state.setPattern(0b1111);
        assertEquals(0b1111, state.getValue());
        assertEquals(LedState.MODE_PATTERN, state.getMode());
    }

}
