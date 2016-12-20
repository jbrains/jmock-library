package org.jmock.test.acceptance;

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

        final Expectations expectations = new Expectations() {{
            allowing(arbitraryInterface).arbitraryMethod(with("::arbitrary parameter::"));
            will(returnValue("::stub return value::"));

            oneOf(arbitraryInterface).arbitraryMethod(with("::arbitrary parameter::"));
            will(returnValue("::expectation return value::"));
        }};

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


