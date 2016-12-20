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

        final Expectations expectations = new Expectations();
        expectations.allowing(arbitraryInterface).arbitraryMethod(expectations.with("::arbitrary parameter::"));
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


