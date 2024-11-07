package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InstrumentTest {

    @Test
    void testInstrumentCreation() {
        MusicItem instrument = new Instrument("Guitar", 250.0, "Electric");
        Instrument castedInstrument = (Instrument) instrument;

        assertEquals("Guitar", castedInstrument.getName(), "Instrument name should be 'Guitar'");
        assertEquals(250.0, castedInstrument.getPrice(), 0.001, "Instrument price should be 250.0");
        assertEquals("Electric", castedInstrument.getType(), "Instrument type should be 'Electric'");
    }
}
