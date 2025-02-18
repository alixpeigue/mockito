/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockitoutil.TestBase;

public class SerializableMethodTest extends TestBase {

    private MockitoMethod method;
    private Method toStringMethod;
    private Class<?>[] args;

    @Before
    public void createMethodToTestWith() throws SecurityException, NoSuchMethodException {
        args = new Class<?>[0];
        toStringMethod = this.getClass().getMethod("toString", args);
        method = new SerializableMethod(toStringMethod);
    }

    @Test
    public void shouldBeSerializable() throws Exception {
        ByteArrayOutputStream serialized = new ByteArrayOutputStream();
        new ObjectOutputStream(serialized).writeObject(method);
    }

    @Test
    public void shouldBeAbleToRetrieveMethodExceptionTypes() throws Exception {
        assertArrayEquals(toStringMethod.getExceptionTypes(), method.getExceptionTypes());
    }

    @Test
    public void shouldBeAbleToRetrieveMethodName() throws Exception {
        assertEquals(toStringMethod.getName(), method.getName());
    }

    @Test
    public void shouldBeAbleToCheckIsArgVargs() throws Exception {
        assertEquals(toStringMethod.isVarArgs(), method.isVarArgs());
    }

    @Test
    public void shouldBeAbleToGetParameterTypes() throws Exception {
        assertArrayEquals(toStringMethod.getParameterTypes(), method.getParameterTypes());
    }

    @Test
    public void shouldBeAbleToGetReturnType() throws Exception {
        assertEquals(toStringMethod.getReturnType(), method.getReturnType());
    }

    @Test
    public void shouldBeEqualForTwoInstances() throws Exception {
        assertTrue(new SerializableMethod(toStringMethod).equals(method));
    }

    @Test
    public void shouldNotBeEqualForSameMethodFromTwoDifferentClasses() throws Exception {
        Method testBaseToStringMethod = String.class.getMethod("toString", args);
        assertFalse(new SerializableMethod(testBaseToStringMethod).equals(method));
    }

    @Test
    public void shouldNotBeEqualToNull() throws Exception {
        assertFalse(method.equals(null));
    }

    @Test
    public void shouldNotBeEqualToOtherType() throws Exception {
        assertFalse(method.equals(new Object()));
    }

    @Test
    public void shouldNotBeEqualForDifferentMethodNamesFromSameClassAndSameArguments()
            throws Exception {
        Method testBaseGetClassMethod = this.getClass().getMethod("getClass", args);
        assertFalse(new SerializableMethod(testBaseGetClassMethod).equals(method));
    }

    @Test
    public void shouldNotBeEqualForSameMethodWithDifferentArguments() throws Exception {
        class TestClass {
            public void method(int param) {}

            public void method() {}
        }
        Method method1 = TestClass.class.getMethod("method", int.class);
        Method method2 = TestClass.class.getMethod("method");
        assertFalse(new SerializableMethod(method1).equals(new SerializableMethod(method2)));
    }
}
