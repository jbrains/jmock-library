/* Copyright (c) 2000-2003, jMock.org. See LICENSE.txt */
package org.jmock.stub;

import org.jmock.dynamic.Invocation;

public class VoidStub extends CallStub {

    public Object invoke(Invocation invocation) throws Throwable {
        return null;
    }

    public StringBuffer writeTo(StringBuffer buffer) {
        return buffer.append("returns <void>");
    }
}
