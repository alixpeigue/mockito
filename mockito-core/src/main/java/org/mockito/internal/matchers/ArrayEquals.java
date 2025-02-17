/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;

import org.mockito.CoverageMeasurement;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ArrayEquals extends Equals {

    public ArrayEquals(Object wanted) {
        super(wanted);
    }

    @Override
    public boolean matches(Object actual) {
        CoverageMeasurement measurement = new CoverageMeasurement("ArrayEquals::matches", 11);
        Object wanted = getWanted();
        if (wanted == null || actual == null) {
            measurement.branch(0);
            return super.matches(actual);
        } else if (wanted instanceof boolean[] && actual instanceof boolean[]) {
            measurement.branch(1);
            return Arrays.equals((boolean[]) wanted, (boolean[]) actual);
        } else if (wanted instanceof byte[] && actual instanceof byte[]) {
            measurement.branch(2);
            return Arrays.equals((byte[]) wanted, (byte[]) actual);
        } else if (wanted instanceof char[] && actual instanceof char[]) {
            measurement.branch(3);
            return Arrays.equals((char[]) wanted, (char[]) actual);
        } else if (wanted instanceof double[] && actual instanceof double[]) {
            measurement.branch(4);
            return Arrays.equals((double[]) wanted, (double[]) actual);
        } else if (wanted instanceof float[] && actual instanceof float[]) {
            measurement.branch(5);
            return Arrays.equals((float[]) wanted, (float[]) actual);
        } else if (wanted instanceof int[] && actual instanceof int[]) {
            measurement.branch(6);
            return Arrays.equals((int[]) wanted, (int[]) actual);
        } else if (wanted instanceof long[] && actual instanceof long[]) {
            measurement.branch(7);
            return Arrays.equals((long[]) wanted, (long[]) actual);
        } else if (wanted instanceof short[] && actual instanceof short[]) {
            measurement.branch(8);
            return Arrays.equals((short[]) wanted, (short[]) actual);
        } else if (wanted instanceof Object[] && actual instanceof Object[]) {
            measurement.branch(9);
            return Arrays.equals((Object[]) wanted, (Object[]) actual);
        }
        measurement.branch(10);
        return false;
    }

    @Override
    public String toString() {
        if (getWanted() != null && getWanted().getClass().isArray()) {
            return appendArray(createObjectArray(getWanted()));
        } else {
            return super.toString();
        }
    }

    private String appendArray(Object[] array) {
        // TODO SF overlap with ValuePrinter
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            out.append(new Equals(array[i]));
            if (i != array.length - 1) {
                out.append(", ");
            }
        }
        out.append("]");
        return out.toString();
    }

    public static Object[] createObjectArray(Object array) {
        if (array instanceof Object[]) {
            return (Object[]) array;
        }
        Object[] result = new Object[Array.getLength(array)];
        for (int i = 0; i < Array.getLength(array); i++) {
            result[i] = Array.get(array, i);
        }
        return result;
    }
}
