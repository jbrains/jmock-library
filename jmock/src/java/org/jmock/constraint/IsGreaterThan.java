/* Copyright (c) 2000-2003, jMock.org. See LICENSE.txt */
package org.jmock.constraint;

import org.jmock.Constraint;

/**
 * Is the value greater than another {@link java.lang.Comparable} value?
 */
public class IsGreaterThan implements Constraint {
    private Comparable _object;

    public IsGreaterThan(Comparable o) {
        _object = o;
    }

    public boolean eval(Object arg) {
        return _object.compareTo(arg) < 0;
    }

    public String toString() {
        return "a value greater than <" + _object + ">";
    }
}
