package com.musicshop.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InstrumentTest {

    @Test
    void testInstrumentCreation() {
        Instrument instrument = new Instrument("Guitar", 150.0, "String");
        assertEquals("Guitar", instrument.getName());
        assertEquals(150.0, instrument.getPrice());
    }

    @Test
    void testInstrumentToString() {
        Instrument instrument = new Instrument("Piano", 1000.0, "Percussion");
        String expected = "Name: Piano, Price: $1000.0, Type: Percussion";
        assertEquals(expected, instrument.toString());
    }
}
