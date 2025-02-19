/*
 * Copyright (c) 2016 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers.text;

import org.mockito.CoverageMeasurement;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.Map;

/**
 * Prints a Java object value in a way humans can read it neatly.
 * Inspired on hamcrest. Used for printing arguments in verification errors.
 */
public class ValuePrinter {

    private ValuePrinter() {}

    /**
     * Prints given value so that it is neatly readable by humans.
     * Handles explosive toString() implementations.
     */
    public static String print(final Object value) {
        CoverageMeasurement measurement = new CoverageMeasurement("ValuePrinter::print", 22);
        if (value == null) {
            measurement.branch(0);
            return "null";
        }
        measurement.branch(1);
        if (value instanceof String) {
            measurement.branch(2);
            return '"' + value.toString() + '"';
        }
        measurement.branch(3);
        if (value instanceof Character) {
            measurement.branch(4);
            return printChar((Character) value);
        }
        measurement.branch(5);
        if (value instanceof Long) {
            measurement.branch(6);
            return value + "L";
        }
        measurement.branch(7);
        if (value instanceof Double) {
            measurement.branch(8);
            return value + "d";
        }
        measurement.branch(9);
        if (value instanceof Float) {
            measurement.branch(10);
            return value + "f";
        }
        measurement.branch(11);
        if (value instanceof Short) {
            measurement.branch(12);
            return "(short) " + value;
        }
        measurement.branch(13);
        if (value instanceof Byte) {
            measurement.branch(14);
            return String.format("(byte) 0x%02X", (Byte) value);
        }
        measurement.branch(15);
        if (value instanceof Map) {
            measurement.branch(16);
            return printMap((Map<?, ?>) value);
        }
        measurement.branch(17);
        if (value.getClass().isArray()) {
            measurement.branch(18);
            return printValues(
                    "[",
                    ", ",
                    "]",
                    new Iterator<Object>() {
                        private int currentIndex = 0;

                        @Override
                        public boolean hasNext() {
                            return currentIndex < Array.getLength(value);
                        }

                        public Object next() {
                            return Array.get(value, currentIndex++);
                        }

                        public void remove() {
                            throw new UnsupportedOperationException(
                                    "cannot remove items from an array");
                        }
                    });
        }
        measurement.branch(19);
        if (value instanceof FormattedText) {
            measurement.branch(20);
            return (((FormattedText) value).getText());
        }
        measurement.branch(21);

        return descriptionOf(value);
    }

    private static String printMap(Map<?, ?> map) {
        StringBuilder result = new StringBuilder();
        Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<?, ?> entry = iterator.next();
            result.append(print(entry.getKey())).append(" = ").append(print(entry.getValue()));
            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
        return "{" + result + "}";
    }

    /**
     * Print values in a nice format, e.g. (1, 2, 3)
     *
     * @param start the beginning of the values, e.g. "("
     * @param separator the separator of values, e.g. ", "
     * @param end the end of the values, e.g. ")"
     * @param values the values to print
     *
     * @return neatly formatted value list
     */
    public static String printValues(
            String start, String separator, String end, Iterator<?> values) {
        if (start == null) {
            start = "(";
        }
        if (separator == null) {
            separator = ",";
        }
        if (end == null) {
            end = ")";
        }

        StringBuilder sb = new StringBuilder(start);
        while (values.hasNext()) {
            sb.append(print(values.next()));
            if (values.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.append(end).toString();
    }

    private static String printChar(char value) {
        StringBuilder sb = new StringBuilder();
        sb.append('\'');
        switch (value) {
            case '"':
                sb.append("\\\"");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                sb.append(value);
        }
        sb.append('\'');
        return sb.toString();
    }

    private static String descriptionOf(Object value) {
        try {
            return String.valueOf(value);
        } catch (RuntimeException e) {
            return value.getClass().getName() + "@" + Integer.toHexString(value.hashCode());
        }
    }
}
