/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.invocation;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.mockito.CoverageMeasurement;
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
        CoverageMeasurement measurement = new CoverageMeasurement("SerializableMethod::equals", 23);
        if (this == obj) {
            measurement.branch(0);
            return true;
        }
        measurement.branch(1);
        if (obj == null) {
            measurement.branch(2);
            return false;
        }
        measurement.branch(3);
        if (getClass() != obj.getClass()) {
            measurement.branch(4);
            return false;
        }
        measurement.branch(5);
        SerializableMethod other = (SerializableMethod) obj;
        if (declaringClass == null) {
            measurement.branch(6);
            if (other.declaringClass != null) {
                measurement.branch(7);
                return false;
            }
            measurement.branch(8);
        } else if (!declaringClass.equals(other.declaringClass)) {
            measurement.branch(9);
            return false;
        }
        measurement.branch(10);
        if (methodName == null) {
            measurement.branch(11);
            if (other.methodName != null) {
                measurement.branch(12);
                return false;
            }
            measurement.branch(13);
        } else if (!methodName.equals(other.methodName)) {
            measurement.branch(14);
            return false;
        }
        measurement.branch(15);
        if (!Arrays.equals(parameterTypes, other.parameterTypes)) {
            measurement.branch(16);
            return false;
        }
        measurement.branch(17);
        if (returnType == null) {
            measurement.branch(18);
            if (other.returnType != null) {
                measurement.branch(19);
                return false;
            }
            measurement.branch(20);
        } else if (!returnType.equals(other.returnType)) {
            measurement.branch(21);
            return false;
        }
        measurement.branch(22);
        return true;
    }
}
