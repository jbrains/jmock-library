package org.jmock.test.acceptance;

import org.jmock.AbstractExpectations;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.syntax.ReceiverClause;
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
        final ReceiverClause atLeastZeroCalls = expectations.atLeast(0);
        final ArbitraryInterface allowing = atLeastZeroCalls.of(arbitraryInterface);
        final String matchParameterForStub = expectations.with("::arbitrary parameter::");
        allowing.arbitraryMethod(matchParameterForStub);
        final Action returnValueActionForStub = AbstractExpectations.returnValue("::stub return value::");
        expectations.will(returnValueActionForStub);

        // oneOf() probably needs to come before with(), but since
        // we have called allowing() earlier in this test, the two
        // statements appear to be able to be safely reordered here
        // in this context.
        final ReceiverClause exactlyOneCall = expectations.exactly(1);
        final ArbitraryInterface oneOf = exactlyOneCall.of(arbitraryInterface);
        final String matchParameterForExpectation = expectations.with("::arbitrary parameter::");
        oneOf.arbitraryMethod(matchParameterForExpectation);
        final Action returnValueActionForExpectation = AbstractExpectations.returnValue("::expectation return value::");
        expectations.will(returnValueActionForExpectation);

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


