package edu.jhu.cs.pl.to_do.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemTests {
    @Test
    public void trivialTest() {
        var item = new Item();
        assertEquals(0, item.getId());
        assertEquals("", item.getDescription());
        item.setDescription("These tests are trivial because the there isn’t much business logic");
        assertEquals("These tests are trivial because the there isn’t much business logic", item.getDescription());
    }
}
