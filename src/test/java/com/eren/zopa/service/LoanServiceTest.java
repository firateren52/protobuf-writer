package com.eren.zopa.service;

import com.eren.zopa.model.Lender;
import com.eren.zopa.model.Quote;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LoanServiceTest {

    @Test
    public void getQuote_givenValidLendersAndAmount_thenReturnQuote() {
        // given

        // when
        LoanService lenderService = new LoanService();
        List<Lender> lenders = new ArrayList<>();
        BigDecimal amount = new BigDecimal(500);
        Quote quote = lenderService.getQuote(lenders, amount);

        // then
        assertThat(quote).isNotNull();
    }
}
