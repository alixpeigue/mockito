/*
 * Copyright (c) 2025 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockitoutil.TestBase;

public class ArrayEqualsTest extends TestBase {
    private ArrayEquals arrayEquals;

    @Test
    public void test_both_null() {
        arrayEquals = new ArrayEquals(null);
        assertTrue(arrayEquals.matches(null));
    }

    @Test
    public void test_wanted_null() {
        arrayEquals = new ArrayEquals(null);
        assertFalse(arrayEquals.matches(new int[] {4, 4, 4}));
    }

    @Test
    public void test_actual_null() {
        arrayEquals = new ArrayEquals(new int[] {4, 4, 4});
        assertFalse(arrayEquals.matches(null));
    }
    @Test
    public void test_non_match_arrays() {
        arrayEquals = new ArrayEquals(new boolean[] {true, false, true});
        assertFalse(arrayEquals.matches(new int[] {4, 4, 4}));
    }

    @Test
    public void test_bool_arrays_match() {
        arrayEquals = new ArrayEquals(new boolean[] {true, false, true});
        assertFalse(arrayEquals.matches(new boolean[] {false, false, true}));
    }
}
