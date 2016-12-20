package org.jmock.test.acceptance;

import org.hamcrest.StringDescription;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.ExpectationError;
import org.jmock.test.unit.support.AssertThat;
import org.junit.Test;

import static org.junit.Assert.fail;

public class ConflictingExpectationsTest {
    private Mockery context = new Mockery();

    @Test
    public void sameMethodSameParameters() throws Exception {
        final ArbitraryInterface arbitraryInterface = context.mock(ArbitraryInterface.class);

        try {
            final Expectations expectations = new Expectations() {{
                allowing(arbitraryInterface).arbitraryMethod(with("::arbitrary parameter::"));
                will(returnValue("::stub return value::"));

                oneOf(arbitraryInterface).arbitraryMethod(with("::arbitrary parameter::"));
                will(returnValue("::expectation return value::"));
            }};

            context.checking(expectations);

            arbitraryInterface.arbitraryMethod("::arbitrary parameter::");
            fail("How did you call a method with conflicting expectations?!");
        } catch (ExpectationError expected) {
            AssertThat.stringIncludes(
                    "conflicting expectations message",
                    "conflicting expectations",
                    StringDescription.toString(expected));
        }
    }

    interface ArbitraryInterface {
        String arbitraryMethod(String arbitraryParameter);
    }
}


