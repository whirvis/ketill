package io.ketill.psx;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LightbarTest {

    private Lightbar lightbar;

    @BeforeEach
    void createLightbar() {
        this.lightbar = new Lightbar("lightbar");
    }

    @Test
    void testGetDeviceType() {
        assertSame(Ps4Controller.class, lightbar.getDeviceType());
    }

}
