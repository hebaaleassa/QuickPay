package org.example.payments.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenVersionStoreTest {

    private final TokenVersionStore store = new TokenVersionStore();

    @Test
    void newUserStartsAtVersionOneAndVersionOneTokenIsValid() {
        assertEquals(1, store.getVersion("alice"));
        assertTrue(store.isCurrent("alice", 1));
    }

    @Test
    void incrementMakesOldTokensInvalid() {
        store.increment("alice");

        assertEquals(2, store.getVersion("alice"));
        assertFalse(store.isCurrent("alice", 1));
        assertTrue(store.isCurrent("alice", 2));
    }

    @Test
    void incrementingTwiceGoesUpByOneEachTime() {
        store.increment("alice");
        store.increment("alice");

        assertEquals(3, store.getVersion("alice"));
    }

    @Test
    void incrementingOneUserDoesNotAffectAnother() {
        store.increment("alice");

        assertTrue(store.isCurrent("admin", 1));
    }
}
