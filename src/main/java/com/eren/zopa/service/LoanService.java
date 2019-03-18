package com.eren.zopa.service;

import com.eren.zopa.model.Lender;
import com.eren.zopa.model.Quote;

import java.math.BigDecimal;
import java.util.List;

public class LoanService {
    private final LenderService lenderService = new LenderService();

    Quote getQuote(List<Lender> lenders, BigDecimal amount) {
        List<Lender> matchedLenders = lenderService.findMatchedLenders(lenders, amount);
        //TODO
        return null;
    }
}
