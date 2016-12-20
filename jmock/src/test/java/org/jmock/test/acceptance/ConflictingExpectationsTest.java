package org.jmock.test.acceptance;

import org.jmock.AbstractExpectations;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.is;

public class ConflictingExpectationsTest {
    private Mockery context = new Mockery();

    @Test
    public void sameMethodSameParameters() throws Exception {
        final ArbitraryInterface arbitraryInterface = context.mock(ArbitraryInterface.class);

        // Expectations is VERY state-sensitive, so assume that all attempts
        // to re-order statements (such as by extracting variables) will fail.
        final Expectations expectations = new Expectations();

        // allowing() must come before with()
        final ArbitraryInterface allowing = expectations.allowing(arbitraryInterface);
        final String matchParameterForStub = expectations.with("::arbitrary parameter::");
        allowing.arbitraryMethod(matchParameterForStub);
        expectations.will(AbstractExpectations.returnValue("::stub return value::"));

        expectations.oneOf(arbitraryInterface).arbitraryMethod(expectations.with("::arbitrary parameter::"));
        expectations.will(AbstractExpectations.returnValue("::expectation return value::"));

        context.checking(expectations);

        // Evidently, the current behavior is a race condition
        // that favors first-come, first-served.
        Assert.assertThat(
                arbitraryInterface.arbitraryMethod("::arbitrary parameter::"),
                is("::stub return value::")
        );
    }

    interface ArbitraryInterface {
        String arbitraryMethod(String arbitraryParameter);
    }
}


