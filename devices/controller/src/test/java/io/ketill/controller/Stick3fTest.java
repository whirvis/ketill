package io.ketill.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Stick3fTest {

    private Stick3f stick;
    
    @BeforeEach
    void setup() {
        this.stick = new Stick3f();
    }

    @Test
    void xyz() {
        assertEquals(0.0F, stick.x());
        assertEquals(0.0F, stick.y());
        assertEquals(0.0F, stick.z());
    }

    @Test
    void buttons() {
        assertNotNull(stick.up());
        assertNotNull(stick.down());
        assertNotNull(stick.left());
        assertNotNull(stick.right());
    }

}
