package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Trigger1fTest {

    private Trigger1f trigger;

    @BeforeEach
    void setup() {
        this.trigger = new Trigger1f();
    }

    @Test
    void force() {
        trigger.force = 1.0F;
        assertEquals(1.0F, trigger.force());
        trigger.force = 0.0F;
        assertEquals(0.0F, trigger.force());
    }

}
