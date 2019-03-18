package com.eren.zopa.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

public class QuoteTest {

    @Test
    public void toString_givenValidParams_returnString() {
        String expectedString = "Requested amount: £1000\n" +
                "Rate: 7.0%\n" +
                "Monthly repayment: £30.78\n" +
                "Total repayment: £1108.10";
        BigDecimal a = BigDecimal.valueOf(30.78);
        Quote quote = new Quote(BigDecimal.valueOf(1000), BigDecimal.valueOf(7.0), BigDecimal.valueOf(30.78), BigDecimal.valueOf(1108.10));
        Assertions.assertThat(quote.toString()).isEqualTo(expectedString);
    }
}
