/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.mockito.exceptions.base.MockitoException;
import org.mockito.internal.creation.SuspendMethod;

public class SerializableMethod implements Serializable, MockitoMethod {

    private static final long serialVersionUID = 6005610965006048445L;

    private final Class<?> declaringClass;
    private final String methodName;
    private final Class<?>[] parameterTypes;
    private final Class<?> returnType;
    private final Class<?>[] exceptionTypes;
    private final boolean isVarArgs;
    private final boolean isAbstract;

    private transient volatile Method method;

    public SerializableMethod(Method method) {
        this.method = method;
        declaringClass = method.getDeclaringClass();
        methodName = method.getName();
        parameterTypes = SuspendMethod.trimSuspendParameterTypes(method.getParameterTypes());
        returnType = method.getReturnType();
        exceptionTypes = method.getExceptionTypes();
        isVarArgs = method.isVarArgs();
        isAbstract = (method.getModifiers() & Modifier.ABSTRACT) != 0;
    }

    @Override
    public String getName() {
        return methodName;
    }

    @Override
    public Class<?> getReturnType() {
        return returnType;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        return exceptionTypes;
    }

    @Override
    public boolean isVarArgs() {
        return isVarArgs;
    }

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    @Override
    public Method getJavaMethod() {
        if (method != null) {
            return method;
        }
        try {
            method = declaringClass.getDeclaredMethod(methodName, parameterTypes);
            return method;
        } catch (SecurityException e) {
            String message =
                    String.format(
                            "The method %1$s.%2$s is probably private or protected and cannot be mocked.\n"
                                    + "Please report this as a defect with an example of how to reproduce it.",
                            declaringClass, methodName);
            throw new MockitoException(message, e);
        } catch (NoSuchMethodException e) {
            String message =
                    String.format(
                            "The method %1$s.%2$s does not exists and you should not get to this point.\n"
                                    + "Please report this as a defect with an example of how to reproduce it.",
                            declaringClass, methodName);
            throw new MockitoException(message, e);
        }
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        // If the two SerializableMethods refer to the same object, then they are equal
        if (this == obj) {
            return true;
        }
        // No SerializableMethod is equal to null
        if (obj == null) {
            return false;
        }
        // A SerializableMethod (or any ob its subtypes) can only be equal of an object of exactly
        // the same type
        if (getClass() != obj.getClass()) {
            return false;
        }
        SerializableMethod other = (SerializableMethod) obj;

        // The two SerializableMethod must have equal (possibly both null) declaring classes to be
        // equal
        if (declaringClass == null) {
            // Note : unattainable code, a Method cannot have a null declaringClass
            if (other.declaringClass != null) {
                return false;
            }
        } else if (!declaringClass.equals(other.declaringClass)) {
            return false;
        }

        // The two SerializableMethod must have equal (possibly both null) method names to be equal
        if (methodName == null) {
            // Note : unattainable code, a method cannot have a null methodName
            if (other.methodName != null) {
                return false;
            }
        } else if (!methodName.equals(other.methodName)) {
            return false;
        }

        // the two SerializableMethod must have equal element by element parameter types arrays to
        // be equal
        if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
            return false;
        }

        // The two SerializableMethod must have equal (possibly both null) return types to be equal
        if (returnType == null) {
            // Note : unattainable code, a method cannot have a null returnType
            if (other.returnType != null) {
                return false;
            }
        } else if (!returnType.equals(other.returnType)) {
            // Note: unattainable code
            // methods already have the same name and same parameter types and same declaring class
            // so they cannot have different return type because having method differ only by their
            // return type is illegal in java :
            // class ClassName {
            //     void method() {}
            //     int method() {]
            // }
            // is illegal.
            return false;
        }

        // If all conditions are met, serializable methods are equal
        return true;
    }
}
